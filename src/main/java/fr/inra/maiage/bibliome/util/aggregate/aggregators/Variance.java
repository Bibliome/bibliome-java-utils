package fr.inra.maiage.bibliome.util.aggregate.aggregators;

import fr.inra.maiage.bibliome.util.aggregate.Aggregator;
import fr.inra.maiage.bibliome.util.aggregate.AggregatorFactory;

public class Variance extends NumberAggregator {
	private final String format;
	private int count = 0;
	private double mean = 0.0;
	private double m2 = 0.0;
	
	public Variance(boolean lax, String format) {
		super(lax);
		this.format = format;
	}

	@Override
	public String get() {
		return String.format(format, m2 / count);
	}
	
	// Welford's Online algorithm
	@Override
	protected void add(long value) {
		count++;
		double delta = value - mean;
		mean += delta / count;
		m2 += delta * (value - mean);
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
			return new Variance(lax, format);
		}
	}
}
