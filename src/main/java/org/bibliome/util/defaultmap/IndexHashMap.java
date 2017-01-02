package org.bibliome.util.defaultmap;

import java.util.LinkedHashMap;

/**
 * An index associates each element to an integer value unique within the set. 
 * @author rbossy
 */
public class IndexHashMap<T> extends DefaultMap<T,Integer> {
	public IndexHashMap() {
		super(true, new LinkedHashMap<T,Integer>());
	}

	@Override
	protected Integer defaultValue(T key) {
		return size();
	}
}
