package org.bibliome.util.newprojector.states;

import java.util.Collection;
import java.util.Collections;

import org.bibliome.util.newprojector.State;

/**
 * State for dictionaries that only hold one entry per key.
 * @author rbossy
 *
 * @param <T>
 */
public abstract class SingleValueState<T> extends State<T> {
	protected T value = null;

	/**
	 * Creates a new single value state.
	 */
	protected SingleValueState() {
		super();
	}

	/**
	 * Creates a single value state holding with the specified character and parent state.
	 * @param c
	 * @param parent
	 */
	protected SingleValueState(char c, State<T> parent) {
		super(c, parent);
	}

	@Override
	public Collection<T> getValues() {
		if (value == null)
			return Collections.emptyList();
		return Collections.singletonList(value);
	}

	@Override
	public boolean hasValue() {
		return value != null;
	}
}
