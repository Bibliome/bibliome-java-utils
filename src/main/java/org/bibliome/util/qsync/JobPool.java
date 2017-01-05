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

package org.bibliome.util.qsync;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.ExitTimeoutException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;
import org.ggf.drmaa.Session;

public class JobPool {
	private final Logger logger;
	private final Session session;
	private final Map<String,JobSpecification> currentJobs = new HashMap<String,JobSpecification>();
	private final Collection<JobSpecification> failedJobs = new ArrayList<JobSpecification>();
	private boolean shallStop = false;
	
	public JobPool(Logger logger, Session session) {
		super();
		this.logger = logger;
		this.session = session;
	}

	public JobSpecification createJobSpecification(String source) throws DrmaaException {
		JobTemplate jt = session.createJobTemplate();
		return new JobSpecification(source, jt);
	}
	
	public void runJob(JobSpecification js) throws DrmaaException {
		boolean resubmission = js.getFailures() > 0;
		String jobId = session.runJob(js.getJobTemplate());
		logger.info(js.getJobMessage(resubmission ? "resubmitted" : "submitted"));
		js.setJobId(jobId);
		currentJobs.put(jobId, js);
	}
	
	public void clear() throws DrmaaException {
		logger.info("terminating all remaining jobs");
		session.control(Session.JOB_IDS_SESSION_ALL, Session.TERMINATE);
		currentJobs.clear();
		failedJobs.clear();
		shallStop = false;
	}
	
	public void waitAll(FailAction fail, int interval) throws DrmaaException {
		long start = System.currentTimeMillis();
		while (!currentJobs.isEmpty()) {
			cycle(fail, interval);
			if (shallStop) {
				logger.info("aborting");
			}
		}
		if (!failedJobs.isEmpty()) {
			logger.warning("the following jobs have failed");
			for (JobSpecification js : failedJobs) {
				logger.info(js.getJobMessage("failed"));
			}
			logger.warning("some jobs have failed");
		}
		long time = System.currentTimeMillis() - start;
		logger.info("done in " + time + "ms");
	}

	private void cycle(FailAction fail, int interval) throws DrmaaException {
		List<String> jobIds = new ArrayList<String>(currentJobs.keySet());
		try {
			logger.info("synchronizing " + jobIds.size() + " jobs, see you in " + interval + " seconds");
			session.synchronize(jobIds, interval, false);
		}
		catch (ExitTimeoutException e) {
			// it's supposed to happen...
		}
		for (String id : jobIds) {
			checkJob(fail, currentJobs.get(id));
		}
	}

	private void checkJob(FailAction fail, JobSpecification js) throws DrmaaException {
		String id = js.getJobId();
		int status = session.getJobProgramStatus(id);
		if (status == Session.DONE) {
			JobInfo info = session.wait(id, Session.TIMEOUT_NO_WAIT);
			if (info.wasAborted()) {
				logger.warning(js.getJobMessage("was aborted"));
				fail(fail, js);
				return;
			}
			if (info.hasSignaled()) {
				logger.warning(js.getJobMessage("signalled " + info.getTerminatingSignal()));
				fail(fail, js);
				return;
			}
			if (info.getExitStatus() != 0) {
				logger.warning(js.getJobMessage("exited with status " + info.getExitStatus()));
				fail(fail, js);
				return;
			}
			logger.info(js.getJobMessage("is done"));
			currentJobs.remove(id);
			return;
		}
		if (status == Session.FAILED) {
			logger.warning(js.getJobMessage("failed somehow"));
			fail(fail, js);
		}
	}

	private void fail(FailAction fail, JobSpecification js) throws DrmaaException {
		js.incrFailures();
		currentJobs.remove(js.getJobId());
		fail.fail(this, js);
	}
	
	public void abortWait() {
		shallStop = true;
	}
	
	public void registerFailedJob(JobSpecification js) {
		failedJobs.add(js);
	}

	public Logger getLogger() {
		return logger;
	}
}
