package fr.inra.maiage.bibliome.util.aggregate.aggregators;

import fr.inra.maiage.bibliome.util.aggregate.Aggregator;
import fr.inra.maiage.bibliome.util.aggregate.AggregatorFactory;

public class First implements Aggregator {
	private String value = null;

	@Override
	public void add(String value) {
		if (this.value == null) {
			this.value = value;
		}
	}

	@Override
	public String get() {
		return this.value;
	}
	
	public static class Factory implements AggregatorFactory {
		@Override
		public Aggregator create() {
			return new First();
		}
	}
}
