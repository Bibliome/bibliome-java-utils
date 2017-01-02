package org.bibliome.util.qsync.cli;

public class IntervalBuilder {
	private static final int REASONABLE_INTARVAL = 10;
	
	private int interval = 60;
	private boolean forceInterval = false;
	
	public IntervalBuilder() {
		super();
	}

	public void setInterval(int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("zero or negative interval");
		}
		this.interval = interval;
	}

	public void setForceInterval() {
		this.forceInterval = true;
	}
	
	public int getInterval() {
		if (interval < REASONABLE_INTARVAL && !forceInterval) {
			throw new IllegalArgumentException("the specified interval is not sensible");
		}
		return interval;
	}
}
