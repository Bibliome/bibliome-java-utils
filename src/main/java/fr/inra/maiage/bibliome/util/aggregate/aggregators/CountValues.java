package fr.inra.maiage.bibliome.util.aggregate.aggregators;

import java.util.HashMap;
import java.util.Map;

import fr.inra.maiage.bibliome.util.aggregate.Aggregator;
import fr.inra.maiage.bibliome.util.aggregate.AggregatorFactory;
import fr.inra.maiage.bibliome.util.count.Count;
import fr.inra.maiage.bibliome.util.count.CountStats;
import fr.inra.maiage.bibliome.util.count.Stats;

public class CountValues implements Aggregator {
	private final Stats<String,Count> count;
	private final String format;
	private final String separator;
	
	public CountValues(String format, String separator) {
		super();
		this.count = new CountStats<String>(new HashMap<String,Count>());
		this.format = format;
		this.separator = separator;
	}

	@Override
	public void add(String value) {
		count.incr(value);
	}

	@Override
	public String get() {
		StringBuilder sb = new StringBuilder();
		boolean notFirst = false;
		for (Map.Entry<String,Count> e : count.entryList(true)) {
			if (notFirst) {
				sb.append(separator);
			}
			else {
				notFirst = true;
			}
			sb.append(String.format(format, e.getKey(), e.getValue().get()));
		}
		return sb.toString();
	}
	
	public static class Factory implements AggregatorFactory {
		private final String format;
		private final String separator;
		
		public Factory(String format, String separator) {
			super();
			this.format = format;
			this.separator = separator;
		}

		@Override
		public Aggregator create() {
			return new CountValues(format, separator);
		}
	}
}
