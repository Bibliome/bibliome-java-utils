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
