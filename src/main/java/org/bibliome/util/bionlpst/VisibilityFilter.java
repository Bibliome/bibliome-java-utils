package org.bibliome.util.bionlpst;

import org.bibliome.util.filters.Filter;

public class VisibilityFilter<S extends Sourced> implements Filter<S> {
	private final Visibility visibility;

	public VisibilityFilter(Visibility visibility) {
		super();
		this.visibility = visibility;
	}

	@Override
	public boolean accept(S x) {
		return x.getVisibility() == visibility;
	}
}
