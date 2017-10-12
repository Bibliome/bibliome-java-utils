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

package fr.inra.maiage.bibliome.util.qsync.cli;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobTemplate;

import fr.inra.maiage.bibliome.util.qsync.JobPool;
import fr.inra.maiage.bibliome.util.qsync.JobSpecification;

public class FileJobSpecificationBuilder implements JobSpecificationBuilder {
	private final List<String> files = new ArrayList<String>();
	
	@Override
	public void parseOpt(String opt, Iterator<String> argIt) {
		files.add(opt);
	}
	
	@Override
	public List<JobSpecification> getJobSpecification(JobPool jp) throws IOException, DrmaaException {
		if (files.isEmpty()) {
			return processStdin(jp);
		}
		else {
			List<JobSpecification> result = new ArrayList<JobSpecification>();
			for (String filename : files) {
				processFile(result, jp, filename);
			}
			return result;
		}
	}
	
	private static void processFile(List<JobSpecification> result, JobPool jp, String filename) throws IOException, DrmaaException {
		Reader r = new FileReader(filename);
		BufferedReader br = new BufferedReader(r);
		processFile(result, jp, filename, br);		
	}
	
	private static List<JobSpecification> processStdin(JobPool jp) throws IOException, DrmaaException {
		Reader r = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(r);
		List<JobSpecification> result = new ArrayList<JobSpecification>();
		processFile(result, jp, "_stdin_", br);
		return result;
	}
	
	private static List<JobSpecification> processFile(List<JobSpecification> result, JobPool jp, String filename, BufferedReader file) throws IOException, DrmaaException {
		int lineno = 0;
		while (true) {
			String line = file.readLine();
			if (line == null) {
				break;
			}
			lineno++;
			String source = filename + ":" + lineno;
			JobSpecification js = processLine(jp, source, line);
			result.add(js);
		}
		return result;
	}
	
	private static JobSpecification processLine(JobPool jp, String source, String line) throws DrmaaException, IOException {
		JobSpecification result = jp.createJobSpecification(source);
		JobTemplate jt = result.getJobTemplate();
		setJobTemplateSpecs(jt, line);
		return result;
	}
	
	private static void setJobTemplateSpecs(JobTemplate jt, String line) throws DrmaaException, IOException {
		int ddash = line.indexOf("--");
		if (ddash != -1) {
			String geOpts = line.substring(0, ddash);
			jt.setNativeSpecification(geOpts);
			line = line.substring(ddash + 2);
		}
		setJobTemplateCommand(jt, line);
	}

	private static void setJobTemplateCommand(JobTemplate jt, String line) throws IOException, DrmaaException {
		Reader r = new StringReader(line);
		StreamTokenizer tokenizer = new StreamTokenizer(r);
		tokenizer.quoteChar('"');
		tokenizer.quoteChar('\'');
		String cmd = null;
		List<String> args = new ArrayList<String>();
		for (int tok = tokenizer.nextToken(); tok != StreamTokenizer.TT_EOF; tok = tokenizer.nextToken()) {
			if (tok == StreamTokenizer.TT_WORD || tok == StreamTokenizer.TT_NUMBER) {
				if (cmd == null) {
					cmd = tokenizer.sval;
				}
				else {
					args.add(tokenizer.sval);
				}
			}
		}
		jt.setRemoteCommand(cmd);
		jt.setArgs(args);
	}
}
