package org.bibliome.util.marshall;

public interface WriteCache<T> {
	boolean contains(T object);
	int get(T object);
	void put(T object, int position);
}
