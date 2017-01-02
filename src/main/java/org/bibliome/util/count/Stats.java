package org.bibliome.util.count;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bibliome.util.defaultmap.DefaultMap;

/**
 * A stats object keeps the count of occurrences of several objects.
 * @author rbossy
 *
 * @param <K>
 * @param <C>
 */
public abstract class Stats<K,C extends Count> extends DefaultMap<K,C> {

	/**
	 * Create a stat object supported by the specified map.
	 * @param map
	 */
	public Stats(Map<K,C> map) {
		super(true, map);
	}
	
	/**
	 * Increase the number of occurrences of the specified object by the specified number.
	 * @param key
	 * @param n
	 */
	public void incr(K key, long n) {
		safeGet(key).incr(n);
	}
	
	/**
	 * Increase the number of occurrences of the specified object by one.
	 * @param key
	 */
	public void incr(K key) {
		safeGet(key).incr();
	}
	
	public void incrAll(Collection<K> keys, long n) {
		for (K key : keys)
			incr(key, n);
	}
	
	public void incrAll(Collection<K> keys) {
		for (K key : keys)
			incr(key);
	}
	
	private static class MapEntryComparator<V> implements Comparator<Map.Entry<?,V>> {
		private final Comparator<V> comparator;

		private MapEntryComparator(Comparator<V> comparator) {
			super();
			this.comparator = comparator;
		}

		@Override
		public int compare(Entry<?, V> a, Map.Entry<?, V> b) {
			return comparator.compare(a.getValue(), b.getValue());
		}
	}

	/**
	 * Returns the list of counted objects sorted by the specified comparator.
	 * @param comparator
	 */
	public List<Map.Entry<K,C>> entryList(Comparator<C> comparator) {
		List<Map.Entry<K,C>> result = new ArrayList<Map.Entry<K,C>>(entrySet());
		Collections.sort(result, new MapEntryComparator<C>(comparator));
		return result;
	}
	
	/**
	 * Returns the list of counted objects sorted by the number of occurrences.
	 * @param reverse
	 */
	public List<Map.Entry<K,C>> entryList(boolean reverse) {
		return entryList(reverse ? Collections.reverseOrder(new Count.CountComparator<C>()) : new Count.CountComparator<C>());
	}
	
	/**
	 * Returns the sum of occurrence of all counted objects.
	 */
	public long sum() {
		long result = 0;
		for (Count c : values())
			result += c.get();
		return result;
	}
	
	public void cutoff(long minCount) {
		Collection<C> counts = values();
		Iterator<C> it = counts.iterator();
		while (it.hasNext())
			if (it.next().get() < minCount)
				it.remove();
	}
}
