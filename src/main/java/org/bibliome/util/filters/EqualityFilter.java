package org.bibliome.util.filters;

public class EqualityFilter<T> implements Filter<T> {
	private final T reference;

	public EqualityFilter(T reference) {
		super();
		this.reference = reference;
	}

	@Override
	public boolean accept(T x) {
		if (x == null)
			return reference == null;
		return x.equals(reference);
	}
}
