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
