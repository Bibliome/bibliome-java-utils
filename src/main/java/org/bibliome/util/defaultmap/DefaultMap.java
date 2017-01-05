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

package org.bibliome.util.defaultmap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bibliome.util.mappers.Mapper;

/**
 * Map with default a value.
 * 
 * @author rbossy
 */
public abstract class DefaultMap<K, V> implements Map<K,V>, Mapper<K,V> {
	private final boolean keepDefaultValue;
	private final Map<K,V> map;

	/**
	 * Creates anew default map
	 * @param keepDefaultValue either to keep the default value when a missing key is required
	 * @param map underlying map
	 */
    public DefaultMap(boolean keepDefaultValue, Map<K,V> map) {
		super();
		this.keepDefaultValue = keepDefaultValue;
		this.map = map;
	}

    /**
     * Returns the default value for the specified key.
     * @param key
     */
    protected abstract V defaultValue(K key);
    
    /**
     * Returns the value associated with the specified key, or the default value if the key is not in the map.
     * @param key
     * @param keepDefaultValue either to put the default value in the map
     */
    public V safeGet(K key, boolean keepDefaultValue) {
    	if (map.containsKey(key))
    		return map.get(key);
    	V result = defaultValue(key);
    	if (keepDefaultValue)
    		map.put(key, result);
    	return result;
    }

    /**
     * Returns the value associated with the specified key, or the default value if the key is not in the map.
     * If this default map was constructed with keepDefaultValue set to true, then the default value is added to the map.
     * @param key
     */
    public V safeGet(K key) {
    	return safeGet(key, keepDefaultValue);
    }

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set<Map.Entry<K,V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return map.equals(o);
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public V put(K key, V value) {
		return map.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K,? extends V> m) {
		map.putAll(m);
	}

	@Override
	public V remove(Object key) {
		return map.remove(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public V map(K x) {
		return safeGet(x);
	}
}
