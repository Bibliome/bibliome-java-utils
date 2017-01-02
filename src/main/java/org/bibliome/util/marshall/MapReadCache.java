package org.bibliome.util.marshall;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Read cache that relies on a Map.
 * @author rbossy
 *
 * @param <T>
 */
public class MapReadCache<T> implements ReadCache<T> {
	private final Map<Integer,T> map;
	
	/**
	 * Creates a read cache that relies on the specified map.
	 * @param map
	 */
	public MapReadCache(Map<Integer,T> map) {
		super();
		this.map = map;
	}
	
	/**
	 * Returns a read cache that relies on a HashMap.
	 */
	public static <T> MapReadCache<T> hashMap() {
		return new MapReadCache<T>(new HashMap<Integer,T>());
	}
	
	/**
	 * Returns a read cache that relies on a HashMap.
	 */
	public static <T> MapReadCache<T> treeMap() {
		return new MapReadCache<T>(new TreeMap<Integer,T>());
	}

	@Override
	public T get(int position) {
		return map.get(position);
	}

	@Override
	public void put(int position, T object) {
		map.put(position, object);
	}

	public Map<Integer,T> getMap() {
		return Collections.unmodifiableMap(map);
	}
}
