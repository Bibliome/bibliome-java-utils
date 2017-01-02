package org.bibliome.util.fragments;

import java.util.Comparator;

final class StableComparator<T> implements Comparator<T> {
	@Override
	public int compare(T a, T b) {
		return Integer.compare(System.identityHashCode(a), System.identityHashCode(b));
	}
}