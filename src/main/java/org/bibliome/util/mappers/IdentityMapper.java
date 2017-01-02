package org.bibliome.util.mappers;

public class IdentityMapper<T extends U,U> implements Mapper<T,U> {
	@Override
	public U map(T x) {
		return x;
	}
}
