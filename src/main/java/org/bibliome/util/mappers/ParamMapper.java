package org.bibliome.util.mappers;

public interface ParamMapper<S,T,P> {
	T map(S x, P param);
}
