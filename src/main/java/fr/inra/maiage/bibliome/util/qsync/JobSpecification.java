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

package fr.inra.maiage.bibliome.util.qsync;

import org.ggf.drmaa.JobTemplate;

public class JobSpecification {
	private final String source;
	private final JobTemplate jobTemplate;
	private String jobId;
	private int failures;
	
	JobSpecification(String source, JobTemplate jobTemplate) {
		super();
		this.source = source;
		this.jobTemplate = jobTemplate;
	}

	public String getSource() {
		return source;
	}

	public JobTemplate getJobTemplate() {
		return jobTemplate;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public int getFailures() {
		return failures;
	}

	public void incrFailures() {
		this.failures++;
	}
	
	String getJobMessage(String message) {
		return "job specified at " + source + " with id " + jobId + " " + message;
	}
}
