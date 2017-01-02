package org.bibliome.util.defaultmap;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default map with HashSet values, backed by a HashMap.
 * @author rbossy
 */
public final class DefaultLinkedHashSetLinkedHashMap<T,U> extends DefaultMap<T,Set<U>> {
	public DefaultLinkedHashSetLinkedHashMap() {
		super(true, new LinkedHashMap<T,Set<U>>());
	}

	@Override
	protected Set<U> defaultValue(T key) {
		return new LinkedHashSet<U>();
	}
}