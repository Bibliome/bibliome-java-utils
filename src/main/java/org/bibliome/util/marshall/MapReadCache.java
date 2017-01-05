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
