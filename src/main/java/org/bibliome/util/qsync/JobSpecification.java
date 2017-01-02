package org.bibliome.util.qsync;

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
