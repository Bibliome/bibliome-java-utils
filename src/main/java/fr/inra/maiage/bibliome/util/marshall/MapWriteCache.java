/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package fr.inra.maiage.bibliome.util.marshall;

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
	private final Map<T,Long> map;
	
	/**
	 * Creates a new write cache that relies on the specified map.
	 * @param map
	 */
	public MapWriteCache(Map<T,Long> map) {
		super();
		this.map = map;
	}
	
	/**
	 * Returns a new write cache that relies on a hash map.
	 */
	public static <T> MapWriteCache<T> hashMap() {
		return new MapWriteCache<T>(new HashMap<T,Long>());
	}
	
	/**
	 * Returns a new write cache that relies on a tree map that uses natural ordering.
	 */
	public static <T> MapWriteCache<T> treeMap() {
		return new MapWriteCache<T>(new TreeMap<T,Long>());
	}

	/**
	 * Returns a new write cache that relies on a tree map that uses the specified comparator.
	 */
	public static <T> MapWriteCache<T> treeMap(Comparator<? super T> comp) {
		return new MapWriteCache<T>(new TreeMap<T,Long>(comp));
	}

	@Override
	public boolean contains(T object) {
		return map.containsKey(object);
	}

	@Override
	public long get(T object) {
		return map.get(object);
	}

	@Override
	public void put(T object, long position) {
		map.put(object, position);
	}

	public Map<T,Long> getMap() {
		return map;
	}
}
