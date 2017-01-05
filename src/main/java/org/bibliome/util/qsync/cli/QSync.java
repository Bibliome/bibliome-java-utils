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

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.util.qsync.FailAction;
import org.bibliome.util.qsync.JobPool;
import org.bibliome.util.qsync.JobSpecification;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;

public class QSync {
	private final FailActionBuilder failActionBuilder = new FailActionBuilder();
	private final IntervalBuilder intervalBuilder = new IntervalBuilder();
	private final LogBuilder logBuilder = new LogBuilder();
	private JobSpecificationBuilder jobSpecBuilder = new FileJobSpecificationBuilder();
	private boolean jobSpecBuilderHasChanged = false;
	
	public static void main(String[] args) throws IOException, DrmaaException {
		QSync inst = new QSync();
		if (inst.parseArgs(args)) {
			JobPool jp = inst.createJobPool();
			inst.run(jp);
		}
	}
	
	private boolean parseArgs(String[] args) throws IOException {
		List<String> argList = Arrays.asList(args);
		Iterator<String> argIt = argList.iterator();
		while (argIt.hasNext()) {
			String opt = argIt.next();
			switch (opt) {
				case "--help": {
					help();
					return false;
				}
				case "--max-resubmit": {
					failActionBuilder.setMaxResubmits(getIntArg(opt, argIt));
					break;
				}
				case "--ignore-failure": {
					failActionBuilder.setIgnore();
					break;
				}
				case "--interval": {
					intervalBuilder.setInterval(getIntArg(opt, argIt));
					break;
				}
				case "--force-interval": {
					intervalBuilder.setForceInterval();
					break;
				}
				case "--log-file": {
					logBuilder.setLogFile(getStringArg(opt, argIt));
					break;
				}
				case "--job-list": {
					if (jobSpecBuilderHasChanged) {
						throw new IllegalArgumentException(opt + " should appear before arguments");
					}
					jobSpecBuilder = new FileJobSpecificationBuilder();
					break;
				}
				case "--job-pattern": {
					if (jobSpecBuilderHasChanged) {
						throw new IllegalArgumentException(opt + " should appear before arguments");
					}
					jobSpecBuilder = new ProductPatternSpecificationBuilder();
					break;
				}
				default: {
					jobSpecBuilder.parseOpt(opt, argIt);
					jobSpecBuilderHasChanged = true;
					break;
				}
			}
		}
		return true;
	}
	
	private static void help() {
		helpOption("--help", "", "print this help and exit");
		helpOption("--max-resubmit", "", "number of times a failed job can be resubmitted (default:0)");
		helpOption("--ignore-failure", "", "ignore failed jobs when the maximun number of resubmits was reached");
		helpOption("--interval", "N", "number of seconds between pollings of the Grid Engine (default: 60)");
		helpOption("--force-interval", "", "don't complain if the polling interval is lower than 10");
		helpOption("--log-file", "FILE", "write log into FILE");
		helpOption("--job-list", "", "read jobs from files (or standard input if none specified)");
		helpOption("--job-pattern", "", "generate jobs from the pattern specified in the command line");
		System.out.println();
		System.out.println("Options specific to --job-list:");
		System.out.println();
		System.out.println("Options specific to --job-pattern:");
		helpOption("--", "", "send previous arguments to the Grid Engine as options");
		helpOption("--variable", "NAME VALUES", "declare a pattern variable NAME with values VALUES (comma spearated)");
		helpOption("--file-variable", "NAME FILE", "declare a pattern variable name with values read from FILE (line by line)");
		helpOption("--stdin-variable", "NAME", "declare a pattern variable name with values read from standard input (line by line)");
		helpOption("--glob-variable", "NAME GLOB", "declare a pattern variable name with values as file paths expanded from GLOB");
		helpOption("--glob-absolute-variable", "NAME GLOB", "declare a pattern variable name with values as absolute paths expanded from GLOB");
	}

	private static void helpOption(String opt, String meta, String text) {
		System.out.format("  %-24s %-11s    %s\n", opt, meta, text);
	}

	public static String getStringArg(String opt, Iterator<String> argIt) {
		if (!argIt.hasNext()) {
			throw new IllegalArgumentException("missing arg for " + opt);
		}
		return argIt.next();
	}
	
	public static int getIntArg(String opt, Iterator<String> argIt) {
		try {
			return Integer.parseInt(getStringArg(opt, argIt));
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("arg for " + opt + " is not integer");
		}
	}

	private void run(JobPool jp) throws DrmaaException {
		FailAction fail = failActionBuilder.getFailAction();
		int interval = intervalBuilder.getInterval();
		try {
			for (JobSpecification js : jobSpecBuilder.getJobSpecification(jp)) {
				jp.runJob(js);
			}
			jp.waitAll(fail, interval);
		}
		catch (IOException|DrmaaException e) {
			jp.getLogger().severe(e.getMessage());
		}
		finally {
			jp.clear();
		}
	}

	private JobPool createJobPool() throws IOException {
		Logger logger = logBuilder.getLogger();
		SessionFactory factory = SessionFactory.getFactory();
		Session session = factory.getSession();
		return new JobPool(logger, session);
	}

}
