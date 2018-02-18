package fr.inra.maiage.bibliome.util.aggregate;

import fr.inra.maiage.bibliome.util.aggregate.aggregators.First;

public enum GroupBy implements AggregatorFactory {
	INSTANCE;

	@Override
	public Aggregator create() {
		return new First();
	}
}
