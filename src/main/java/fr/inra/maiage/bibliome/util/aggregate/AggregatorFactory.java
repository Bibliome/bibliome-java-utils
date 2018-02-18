package fr.inra.maiage.bibliome.util.aggregate;

import fr.inra.maiage.bibliome.util.mappers.Mapper;

public interface AggregatorFactory {
	Aggregator create();
	
	public static Mapper<AggregatorFactory,Aggregator> MAPPER = new Mapper<AggregatorFactory,Aggregator>() {
		@Override
		public Aggregator map(AggregatorFactory x) {
			return x.create();
		}
	};
}
