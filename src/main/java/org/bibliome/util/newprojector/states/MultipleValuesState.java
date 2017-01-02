package org.bibliome.util.newprojector.states;

import java.util.Collection;
import java.util.Collections;

import org.bibliome.util.newprojector.State;

/**
 * State for dictionaries that can hold several entries for one key.
 * @author rbossy
 *
 * @param <T>
 */
public abstract class MultipleValuesState<T> extends State<T> {
	private Collection<T> values = null;
	
	/**
	 * Creates a root multiple values state.
	 */
	protected MultipleValuesState() {
		super();
	}

	/**
	 * Creates a multiple values state with the specified character and parent state.
	 * @param c
	 * @param parent
	 */
	protected MultipleValuesState(char c, State<T> parent) {
		super(c, parent);
	}

	@Override
	protected void addValue(T value, CharSequence key) {
		if (values == null)
			values = newCollection();
		values.add(value);
	}
	
	/**
	 * Creates an entry collection.
	 */
	protected abstract Collection<T> newCollection();

	@Override
	public Collection<T> getValues() {
		return Collections.unmodifiableCollection(values);
	}

	@Override
	public boolean hasValue() {
		return values != null;
	}
}
