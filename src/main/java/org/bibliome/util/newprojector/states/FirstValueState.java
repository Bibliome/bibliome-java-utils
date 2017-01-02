package org.bibliome.util.newprojector.states;

import org.bibliome.util.newprojector.State;

/**
 * State for dictionaries that only hold one entry per key.
 * If several entries are inserted for the same key, then only the first inserted is kept.
 * @author rbossy
 *
 * @param <T>
 */
public class FirstValueState<T> extends SingleValueState<T> {
	/**
	 * Creates a root first value state.
	 */
	public FirstValueState() {
		super();
	}

	private FirstValueState(char c, State<T> parent) {
		super(c, parent);
	}

	@Override
	protected void addValue(T value, CharSequence key) {
		if (this.value == null)
			this.value = value;
	}

	@Override
	protected State<T> newState(char c, State<T> parent) {
		return new FirstValueState<T>(c, parent);
	}
}
