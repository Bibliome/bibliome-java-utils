package fr.inra.maiage.bibliome.util.aggregate.aggregators;

import fr.inra.maiage.bibliome.util.aggregate.Aggregator;
import fr.inra.maiage.bibliome.util.aggregate.AggregatorFactory;

public class FloatSum extends FloatAggregator {
	private double value = 0L;

	public FloatSum(boolean lax) {
		super(lax);
	}

	@Override
	public String get() {
		return Double.toString(value);
	}

	@Override
	protected void add(double value) {
		this.value += value;
	}
	
	public static class Factory implements AggregatorFactory {
		private final boolean lax;
		
		public Factory(boolean lax) {
			super();
			this.lax = lax;
		}


		@Override
		public Aggregator create() {
			return new FloatSum(lax);
		}
	}
}
