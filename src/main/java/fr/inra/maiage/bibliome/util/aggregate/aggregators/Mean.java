package fr.inra.maiage.bibliome.util.aggregate.aggregators;

import fr.inra.maiage.bibliome.util.aggregate.Aggregator;
import fr.inra.maiage.bibliome.util.aggregate.AggregatorFactory;

public class Mean extends NumberAggregator {
	private final String format;
	private long total = 0;
	private int count = 0;
	
	public Mean(boolean lax, String format) {
		super(lax);
		this.format = format;
	}

	@Override
	public String get() {
		return String.format(format, ((double) total) / count);
	}
	
	@Override
	protected void add(long value) {
		total += value;
		count++;
	}
	
	public static class Factory implements AggregatorFactory {
		private final boolean lax;
		private final String format;
		
		public Factory(boolean lax, String format) {
			super();
			this.lax = lax;
			this.format = format;
		}

		@Override
		public Aggregator create() {
			return new Mean(lax, format);
		}
	}
}
