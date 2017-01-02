package org.bibliome.util.newprojector.states;

import org.bibliome.util.newprojector.State;

/**
 * State for dictionaries that only hold one entry per key.
 * If several entries are inserted for the same key, then only the last inserted is kept.
 * @author rbossy
 *
 * @param <T>
 */
public class LastValueState<T> extends SingleValueState<T> {
	/**
	 * Creates a root last value state.
	 */
	public LastValueState() {
		super();
	}

	private LastValueState(char c, State<T> parent) {
		super(c, parent);
	}

	@Override
	protected void addValue(T value, CharSequence key) {
		this.value = value;
	}

	@Override
	protected State<T> newState(char c, State<T> parent) {
		return new LastValueState<T>(c, parent);
	}
}
