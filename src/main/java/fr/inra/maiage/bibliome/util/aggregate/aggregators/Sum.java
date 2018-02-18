package fr.inra.maiage.bibliome.util.aggregate.aggregators;

import fr.inra.maiage.bibliome.util.aggregate.Aggregator;
import fr.inra.maiage.bibliome.util.aggregate.AggregatorFactory;

public class Sum extends NumberAggregator {
	private long value = 0L;

	public Sum(boolean lax) {
		super(lax);
	}

	@Override
	public String get() {
		return Long.toString(value);
	}

	@Override
	protected void add(long value) {
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
			return new Sum(lax);
		}
	}
}
