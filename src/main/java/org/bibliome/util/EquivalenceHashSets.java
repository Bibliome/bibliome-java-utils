package org.bibliome.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Equivalence sets based on hash sets.
 * @author rbossy
 *
 * @param <T>
 */
public class EquivalenceHashSets<T> extends EquivalenceSets<T> {
	/**
	 * Creates an empty equivalence sets based on hash sets.
	 */
	public EquivalenceHashSets() {
		super();
	}

	@Override
	protected Set<T> newSet() {
		return new HashSet<T>();
	}
}
