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

import org.bibliome.util.qsync.FailAction;

public class FailActionBuilder {
	private int maxResubmits = 0;
	private FailAction finalFailAction = FailAction.ABORT_ALL;

	public void setMaxResubmits(int maxResubmits) {
		if (maxResubmits <= 0) {
			throw new IllegalArgumentException("negative resubmits");
		}
		this.maxResubmits = maxResubmits;
	}
	
	public void setIgnore() {
		finalFailAction = FailAction.IGNORE;
	}
	
	public FailAction getFailAction() {
		if (maxResubmits == 0) {
			return finalFailAction;
		}
		return new FailAction.Resubmit(maxResubmits, finalFailAction);
	}
}
