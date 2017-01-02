package org.bibliome.util.qsync.cli;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class ProductMap<K,V> extends AbstractCollection<Map<K,V>> {
	private final Map<K,? extends Collection<V>> matrix;
	private final boolean copy;
	
	public ProductMap(Map<K,? extends Collection<V>> matrix, boolean copy) {
		super();
		this.matrix = matrix;
		this.copy = copy;
	}

	@Override
	public Iterator<Map<K,V>> iterator() {
		return new ProductMapIterator();
	}

	@Override
	public int size() {
		int result = 1;
		for (Collection<V> values : matrix.values()) {
			result *= values.size();
		}
		return result;
	}

	private class ProductMapIterator implements Iterator<Map<K,V>> {
		private final Map<K,Iterator<V>> iterators = new LinkedHashMap<K,Iterator<V>>();
		private final Map<K,V> current = new LinkedHashMap<K,V>();
		
		private ProductMapIterator() {
			for (Map.Entry<K,? extends Collection<V>> e : matrix.entrySet()) {
				K key = e.getKey();
				Collection<V> values = e.getValue();
				Iterator<V> it = values.iterator();
				iterators.put(key, it);
				current.put(key, it.next());
			}
		}

		@Override
		public boolean hasNext() {
			for (Iterator<V> it : iterators.values()) {
				if (it.hasNext()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public Map<K,V> next() {
			for (Map.Entry<K,Iterator<V>> e : iterators.entrySet()) {
				K key = e.getKey();
				Iterator<V> it = e.getValue();
				if (it.hasNext()) {
					V value = it.next();
					current.put(key, value);
					if (copy) {
						return new LinkedHashMap<K,V>(current);
					}
					return current;
				}
				Collection<V> values = matrix.get(key);
				it = values.iterator();
				e.setValue(it);
				current.put(key, it.next());
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
