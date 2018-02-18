package fr.inra.maiage.bibliome.util.aggregate.aggregators;

import fr.inra.maiage.bibliome.util.aggregate.Aggregator;
import fr.inra.maiage.bibliome.util.aggregate.AggregatorFactory;

public class CountEmpty implements Aggregator {
	private long count = 0;

	@Override
	public void add(String value) {
		if (value.isEmpty()) {
			count++;
		}
	}

	@Override
	public String get() {
		return Long.toString(count);
	}
	
	public static class Factory implements AggregatorFactory {
		@Override
		public Aggregator create() {
			return new CountEmpty();
		}
	}
}
