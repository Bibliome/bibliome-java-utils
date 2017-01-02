package org.bibliome.util.mappers;

import java.util.Map;

public class MapMapper<T,U> implements Mapper<T,U> {
	private final Map<? super T,? extends U> map;

	public MapMapper(Map<? super T,? extends U> map) {
		super();
		this.map = map;
	}

	@Override
	public U map(T x) {
		return map.get(x);
	}
}
