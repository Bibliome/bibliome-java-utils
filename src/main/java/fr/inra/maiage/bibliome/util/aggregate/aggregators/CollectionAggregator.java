package fr.inra.maiage.bibliome.util.aggregate.aggregators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.TreeSet;

import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.aggregate.Aggregator;
import fr.inra.maiage.bibliome.util.aggregate.AggregatorFactory;

public class CollectionAggregator implements Aggregator {
	private final Collection<String> value;
	private final String separator;
	
	public CollectionAggregator(Collection<String> value, String separator) {
		super();
		this.value = value;
		this.separator = separator;
	}

	@Override
	public void add(String value) {
		this.value.add(value);
	}

	@Override
	public String get() {
		return Strings.join(value, separator);
	}
	
	public static enum CollectionFactory {
		LIST {
			@Override
			public Collection<String> create() {
				return new ArrayList<String>();
			}
		},
		
		SET {
			@Override
			public Collection<String> create() {
				return new LinkedHashSet<String>();
			}
		},
		
		SORTED_SET {
			@Override
			public Collection<String> create() {
				return new TreeSet<String>();
			}
		};
		
		public abstract Collection<String> create();
	}
	
	public static class Factory implements AggregatorFactory {
		private final CollectionFactory collectionFactory;
		private final String separator;
		
		public Factory(CollectionFactory collectionFactory, String separator) {
			super();
			this.collectionFactory = collectionFactory;
			this.separator = separator;
		}

		@Override
		public Aggregator create() {
			return new CollectionAggregator(collectionFactory.create(), separator);
		}
	}
}
