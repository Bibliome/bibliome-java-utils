package org.bibliome.util.newprojector.states;

import java.util.ArrayList;
import java.util.Collection;

import org.bibliome.util.newprojector.State;

/**
 * State for dictionaries holding all entries with the same key in the order of insertion.
 * @author rbossy
 *
 * @param <T>
 */
public class AllValuesState<T> extends MultipleValuesState<T> {
	/**
	 * Creates a root all values state.
	 */
	public AllValuesState() {
		super();
	}

	private AllValuesState(char c, State<T> parent) {
		super(c, parent);
	}

	@Override
	protected Collection<T> newCollection() {
		return new ArrayList<T>(1);
	}

	@Override
	protected State<T> newState(char c, State<T> parent) {
		return new AllValuesState<T>(c, parent);
	}
}
