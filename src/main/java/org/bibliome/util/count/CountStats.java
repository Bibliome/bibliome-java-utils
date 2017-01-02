package org.bibliome.util.count;

import java.util.Map;

/**
 * Stat that counts objects with the basic Count.
 * The default value is a new zero occurrences count.
 * @author rbossy
 *
 * @param <K>
 */
public class CountStats<K> extends Stats<K,Count> {
	/**
	 * Creates a new count stats supported by the specified map.
	 * @param map
	 */
	public CountStats(Map<K,Count> map) {
		super(map);
	}

	@Override
	protected Count defaultValue(K key) {
		return new Count();
	}
}
