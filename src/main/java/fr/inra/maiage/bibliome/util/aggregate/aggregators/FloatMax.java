package fr.inra.maiage.bibliome.util.aggregate.aggregators;

import fr.inra.maiage.bibliome.util.aggregate.Aggregator;
import fr.inra.maiage.bibliome.util.aggregate.AggregatorFactory;

public class FloatMax extends FloatAggregator {
	private double value = Double.NEGATIVE_INFINITY;

	public FloatMax(boolean lax) {
		super(lax);
	}

	@Override
	public String get() {
		return Double.toString(value);
	}

	@Override
	protected void add(double value) {
		if (value > this.value) {
			this.value = value;
		}
	}
	
	public static class Factory implements AggregatorFactory {
		private final boolean lax;
		
		public Factory(boolean lax) {
			super();
			this.lax = lax;
		}


		@Override
		public Aggregator create() {
			return new FloatMax(lax);
		}
	}
}
