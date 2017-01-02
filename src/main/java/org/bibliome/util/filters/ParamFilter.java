package org.bibliome.util.filters;

public interface ParamFilter<T,P> {
	boolean accept(T x, P param);
}
