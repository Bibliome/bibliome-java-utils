package org.bibliome.util.marshall;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Write cache that relies on a Map.
 * @author rbossy
 *
 * @param <T>
 */
public class MapWriteCache<T> implements WriteCache<T> {
	private final Map<T,Integer> map;
	
	/**
	 * Creates a new write cache that relies on the specified map.
	 * @param map
	 */
	public MapWriteCache(Map<T,Integer> map) {
		super();
		this.map = map;
	}
	
	/**
	 * Returns a new write cache that relies on a hash map.
	 */
	public static <T> MapWriteCache<T> hashMap() {
		return new MapWriteCache<T>(new HashMap<T,Integer>());
	}
	
	/**
	 * Returns a new write cache that relies on a tree map that uses natural ordering.
	 */
	public static <T> MapWriteCache<T> treeMap() {
		return new MapWriteCache<T>(new TreeMap<T,Integer>());
	}

	/**
	 * Returns a new write cache that relies on a tree map that uses the specified comparator.
	 */
	public static <T> MapWriteCache<T> treeMap(Comparator<? super T> comp) {
		return new MapWriteCache<T>(new TreeMap<T,Integer>(comp));
	}

	@Override
	public boolean contains(T object) {
		return map.containsKey(object);
	}

	@Override
	public int get(T object) {
		return map.get(object);
	}

	@Override
	public void put(T object, int position) {
		map.put(object, position);
	}

	public Map<T,Integer> getMap() {
		return map;
	}
}
