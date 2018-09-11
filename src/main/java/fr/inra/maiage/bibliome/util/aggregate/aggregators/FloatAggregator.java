package fr.inra.maiage.bibliome.util.aggregate.aggregators;

import fr.inra.maiage.bibliome.util.aggregate.Aggregator;

public abstract class FloatAggregator implements Aggregator {
	private final boolean lax;

	protected FloatAggregator(boolean lax) {
		super();
		this.lax = lax;
	}

	@Override
	public void add(String value) {
		double doubleValue = getDoubleValue(value);
		add(doubleValue);
	}

	protected abstract void add(double value);
	
	private double getDoubleValue(String value) {
		try {
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e) {
			if (lax) {
				return 0.0;
			}
			throw e;
		}
	}
}
