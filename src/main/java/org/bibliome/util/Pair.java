package org.bibliome.util;

import java.util.Comparator;

/**
 * Immutable pair of objects.
 * @author rbossy
 *
 * @param <T>
 * @param <U>
 */
public class Pair<T,U> {
	public final T first;
	public final U second;
	
	/**
	 * Creates a new pair of objects.
	 * @param first
	 * @param second
	 */
	public Pair(T first, U second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	private static class FirstPairComparator<T,U> implements Comparator<Pair<T,U>> {
		private final Comparator<T> comparator;

		private FirstPairComparator(Comparator<T> comparator) {
			super();
			this.comparator = comparator;
		}

		@Override
		public int compare(Pair<T,U> a, Pair<T,U> b) {
			return comparator.compare(a.first, b.first);
		}
	}
	
	private static class SecondPairComparator<T,U> implements Comparator<Pair<T,U>> {
		private final Comparator<U> comparator;

		private SecondPairComparator(Comparator<U> comparator) {
			super();
			this.comparator = comparator;
		}

		@Override
		public int compare(Pair<T,U> a, Pair<T,U> b) {
			return comparator.compare(a.second, b.second);
		}
	}
	
	/**
	 * Returns a pair comparator that compares the first element of the pairs.
	 * @param <T>
	 * @param <U>
	 * @param comparator
	 */
	public static <T,U> Comparator<Pair<T,U>> firstComparator(Comparator<T> comparator) {
		return new FirstPairComparator<T, U>(comparator);
	}
	
	
	/**
	 * Returns a pair comparator that compares the second element of the pairs.
	 * @param <T>
	 * @param <U>
	 * @param comparator
	 */
	public static <T,U> Comparator<Pair<T,U>> secondComparator(Comparator<U> comparator) {
		return new SecondPairComparator<T, U>(comparator);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Pair))
			return false;
		@SuppressWarnings("rawtypes")
		Pair other = (Pair) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "{" + first + ',' + second + '}';
	}
}
