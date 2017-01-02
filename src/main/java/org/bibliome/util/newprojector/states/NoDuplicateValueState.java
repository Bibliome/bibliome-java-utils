package org.bibliome.util.newprojector.states;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.bibliome.util.newprojector.State;

/**
 * State for dictionaries holding multiple values for a key but no duplicate entries.
 * @author rbossy
 *
 * @param <T>
 */
public class NoDuplicateValueState<T> extends MultipleValuesState<T> {
	/**
	 * Creates a root no duplicate value state.
	 */
	public NoDuplicateValueState() {
		super();
	}

	private NoDuplicateValueState(char c, State<T> parent) {
		super(c, parent);
	}

	@Override
	protected Collection<T> newCollection() {
		return new LinkedHashSet<T>(2);
	}

	@Override
	protected State<T> newState(char c, State<T> parent) {
		return new NoDuplicateValueState<T>(c, parent);
	}
}
