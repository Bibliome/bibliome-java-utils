package org.bibliome.util.defaultmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Default map with ArrayList values, backed by a HashMap.
 * @author rbossy
 *
 * @param <K>
 * @param <V>
 */
public class DefaultArrayListHashMap<K,V> extends DefaultMap<K,List<V>> {
	public DefaultArrayListHashMap() {
		super(true, new HashMap<K,List<V>>());
	}

	@Override
	protected List<V> defaultValue(K key) {
		return new ArrayList<V>();
	}
}
