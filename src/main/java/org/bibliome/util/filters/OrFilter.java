package org.bibliome.util.filters;

public class OrFilter<T> implements Filter<T> {
	private final Filter<T> left;
	private final Filter<T> right;
	
	public OrFilter(Filter<T> left, Filter<T> right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean accept(T x) {
		return left.accept(x) || right.accept(x);
	}
}
