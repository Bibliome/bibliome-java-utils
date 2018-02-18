package fr.inra.maiage.bibliome.util.aggregate.aggregators;

import fr.inra.maiage.bibliome.util.aggregate.Aggregator;
import fr.inra.maiage.bibliome.util.aggregate.AggregatorFactory;

public class Min extends NumberAggregator {
	private long value = Long.MAX_VALUE;

	public Min(boolean lax) {
		super(lax);
	}

	@Override
	public String get() {
		return Long.toString(value);
	}

	@Override
	protected void add(long value) {
		if (value < this.value) {
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
			return new Min(lax);
		}
	}
}
