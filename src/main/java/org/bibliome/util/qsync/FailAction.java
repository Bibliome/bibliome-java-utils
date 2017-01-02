package org.bibliome.util.qsync;

import org.ggf.drmaa.DrmaaException;


public interface FailAction {
	void fail(JobPool jobPool, JobSpecification js) throws DrmaaException;
	
	public static final FailAction ABORT_ALL = new FailAction() {
		@Override
		public void fail(JobPool jobPool, JobSpecification js) throws DrmaaException {
			jobPool.registerFailedJob(js);
			jobPool.abortWait();
		}
	};
	
	public static final FailAction IGNORE = new FailAction() {
		@Override
		public void fail(JobPool jobPool, JobSpecification js) throws DrmaaException {
			jobPool.registerFailedJob(js);
		}
	};
	
	public static class Resubmit implements FailAction {
		private final int maxResubmits;
		private final FailAction finalFailAction;

		public Resubmit(int maxResubmits, FailAction finalFailAction) {
			super();
			this.maxResubmits = maxResubmits;
			this.finalFailAction = finalFailAction;
		}

		@Override
		public void fail(JobPool jobPool, JobSpecification js) throws DrmaaException {
			if (js.getFailures() > maxResubmits) {
				finalFailAction.fail(jobPool, js);
			}
			else {
				jobPool.runJob(js);
			}
		}
	}
}
