package org.bibliome.util.defaultmap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Default map with HashSet values, backed by a HashMap.
 * @author rbossy
 */
public final class DefaultHashSetHashMap<T,U> extends DefaultMap<T,Set<U>> {
	public DefaultHashSetHashMap() {
		super(true, new HashMap<T,Set<U>>());
	}

	@Override
	protected Set<U> defaultValue(T key) {
		return new HashSet<U>();
	}
	
}