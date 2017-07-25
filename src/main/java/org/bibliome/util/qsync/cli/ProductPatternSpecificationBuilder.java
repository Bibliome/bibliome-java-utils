/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.bibliome.util.qsync.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bibliome.util.format.FormatSequence;
import org.bibliome.util.qsync.JobPool;
import org.bibliome.util.qsync.JobSpecification;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobTemplate;

public class ProductPatternSpecificationBuilder implements JobSpecificationBuilder {
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("%(.*?)%");
	
	private FormatSequence geOptionsFormat = null;
	private FormatSequence commandFormat = null;
	private List<FormatSequence> argsFormat = new ArrayList<FormatSequence>();
	private final Map<String,List<String>> variables = new LinkedHashMap<String,List<String>>();

	@Override
	public void parseOpt(String opt, Iterator<String> argIt) throws IOException {
		switch (opt) {
			case "--": {
				if (geOptionsFormat != null) {
					throw new IllegalArgumentException("second --");
				}
				if (commandFormat == null) {
					throw new IllegalArgumentException("-- before any argument");
				}
				geOptionsFormat = new FormatSequence();
				geOptionsFormat.addAll(commandFormat, " ");
				for (FormatSequence fs : argsFormat) {
					geOptionsFormat.addAll(fs, " ");
				}
				commandFormat = null;
				argsFormat.clear();
				break;
			}
			case "--variable": {
				addVariable(opt, argIt, ValuesFactory.EXPLICIT);
				break;
			}
			case "--file-variable": {
				addVariable(opt, argIt, ValuesFactory.FILE);
				break;
			}
			case "--stdin-variable": {
				addVariable(opt, argIt, ValuesFactory.STDIN);
				break;
			}
			case "--glob-variable": {
				addVariable(opt, argIt, ValuesFactory.GLOB);
				break;
			}
			case "--glob-absolute-variable": {
				addVariable(opt, argIt, ValuesFactory.GLOB_ABSOLUTE);
				break;
			}
			default: {
				FormatSequence fs = new FormatSequence(opt, VARIABLE_PATTERN);
				if (commandFormat == null) {
					commandFormat = fs;
				}
				else {
					argsFormat.add(fs);
				}
				break;
			}
		}
	}

	private void addVariable(String opt, Iterator<String> argIt, ValuesFactory valuesFactory) throws IOException {
		String name = QSync.getStringArg(opt, argIt);
		if (variables.containsKey(name)) {
			throw new IllegalArgumentException("duplicate variable " + name);
		}
		String sValues = QSync.getStringArg(opt, argIt);
		List<String> values = valuesFactory.getValues(sValues);
		variables.put(name, values);
	}
	
	private static enum ValuesFactory {
		EXPLICIT {
			@Override
			List<String> getValues(String arg) {
				String[] array = arg.split(",");
				return Arrays.asList(array);
			}
		},
		
		GLOB {
			@Override
			List<String> getValues(String arg) throws IOException {
				return getGlobValues(arg, false);
			}
		},
		
		GLOB_ABSOLUTE {
			@Override
			List<String> getValues(String arg) throws IOException {
				return getGlobValues(arg, false);
			}
		},
		
		FILE {
			@Override
			List<String> getValues(String arg) throws IOException {
				try (Reader r = new FileReader(arg)) {
					return getValues(r);
				}
			}
		},
		
		STDIN {
			@Override
			List<String> getValues(String arg) throws IOException {
				Reader r = new InputStreamReader(System.in);
				return getValues(r);
			}
		};
		
		abstract List<String> getValues(String arg) throws IOException;

		private static List<String> getGlobValues(String arg, boolean absolute) throws IOException {
			List<String> result = new ArrayList<String>();
			Path dir = new File(arg).toPath();
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "glob:" + arg)) {
				for (Path path : stream) {
					Path pathValue = absolute ? path.toAbsolutePath() : path.getFileName();
					String value = pathValue.toString();
					result.add(value);
				}
			}
			return result;
		}
		
		static List<String> getValues(Reader r) throws IOException {
			List<String> result = new ArrayList<String>();
			BufferedReader br = new BufferedReader(r);
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				result.add(line.trim());
			}
			return result;
		}
	}

	@Override
	public List<JobSpecification> getJobSpecification(JobPool jp) throws IOException, DrmaaException {
		if (commandFormat == null) {
			throw new IllegalArgumentException("missing remote command (and args)");
		}
		Collection<Map<String,String>> prod = new ProductMap<String,String>(variables, false);
		List<JobSpecification> result = new ArrayList<JobSpecification>(prod.size());
		FormatSequence sourceFormat = getSourceFormatSequence();
		for (Map<String,String> vars : prod) {
			JobSpecification js = jp.createJobSpecification(sourceFormat.build(vars, null));
			JobTemplate jt = js.getJobTemplate();
			setJobTemplateSpecs(jt, vars);
			result.add(js);
		}
		return result;
	}
	
	private void setJobTemplateSpecs(JobTemplate jt, Map<String,String> vars) throws DrmaaException {
		String cmd = commandFormat.build(vars, null);
		jt.setRemoteCommand(cmd);
		List<String> args = getArgs(vars);
		jt.setArgs(args);
		if (geOptionsFormat != null) {
			String geOpts = geOptionsFormat.build(vars, null);
			jt.setNativeSpecification(geOpts);
		}
	}
	
	private List<String> getArgs(Map<String,String> vars) {
		List<String> result = new ArrayList<String>(argsFormat.size());
		for (FormatSequence af : argsFormat) {
			String a = af.build(vars, null);
			result.add(a);
		}
		return result;
	}
	
	private FormatSequence getSourceFormatSequence() {
		FormatSequence result = new FormatSequence();
		boolean notFirst = false;
		for (String var : variables.keySet()) {
			if (notFirst) {
				result.addConstant(",");
			}
			else {
				notFirst = true;
			}
			result.addConstant(var);
			result.addConstant("=");
			result.addVariable(var);
		}
		return result;
	}
}
