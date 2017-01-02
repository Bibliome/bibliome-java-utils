package org.bibliome.util.qsync.cli;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.qsync.JobPool;
import org.bibliome.util.qsync.JobSpecification;
import org.ggf.drmaa.DrmaaException;

public interface JobSpecificationBuilder {
	void parseOpt(String opt, Iterator<String> argIt) throws IOException;
	List<JobSpecification> getJobSpecification(JobPool jp) throws IOException, DrmaaException;
}
