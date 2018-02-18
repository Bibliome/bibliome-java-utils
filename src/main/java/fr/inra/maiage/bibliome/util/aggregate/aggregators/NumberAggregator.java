package fr.inra.maiage.bibliome.util.aggregate.aggregators;

import fr.inra.maiage.bibliome.util.aggregate.Aggregator;

public abstract class NumberAggregator implements Aggregator {
	private final boolean lax;

	protected NumberAggregator(boolean lax) {
		super();
		this.lax = lax;
	}

	@Override
	public void add(String value) {
		long longValue = getLongValue(value);
		add(longValue);
	}

	protected abstract void add(long value);
	
	private long getLongValue(String value) {
		try {
			return Long.parseLong(value);
		}
		catch (NumberFormatException e) {
			if (lax) {
				return 0L;
			}
			throw e;
		}
	}
}
