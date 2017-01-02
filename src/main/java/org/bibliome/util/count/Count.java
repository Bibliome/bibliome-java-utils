package org.bibliome.util.count;

import java.util.Comparator;

/**
 * Occurrence count.
 * @author rbossy
 *
 */
public class Count {
	private long value = 0;
	
	/**
	 * Creates a count with zero occurrences.
	 */
	public Count() {}
	
	/**
	 * Increase the number of occurrences by the specified number.
	 * @param n
	 */
	public void incr(long n) {
		value += n;
	}
	
	/**
	 * Increase the number of occurrences by one.
	 */
	public void incr() {
		value++;
	}
	
	/**
	 * Returns the number of occurrences.
	 */
	public long get() {
		return value;
	}

	/**
	 * Compares count objects by the number of occurrences.
	 * @author rbossy
	 * @param <C>
	 */
	public static class CountComparator<C extends Count> implements Comparator<C> {
		@Override
		public int compare(C a, C b) {
			return Long.compare(a.get(), b.get());
		}
	}
}
