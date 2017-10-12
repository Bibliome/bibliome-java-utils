/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package fr.inra.maiage.bibliome.util.newprojector;

import java.util.Collection;

/**
 * Trie state.
 * @author rbossy
 *
 * @param <T>
 */
public abstract class State<T> {
	private final char c;
	private final State<T> nextSibling;
	private State<T> firstChild = null;

	/**
	 * Creates a state with the specified character and parent state.
	 * @param c
	 * @param parent
	 */
	protected State(char c, State<T> parent) {
		this.c = c;
		this.nextSibling = parent.firstChild;
		parent.firstChild = this;
	}
	
	/**
	 * Creates a root state.
	 */
	protected State() {
		c = 0;
		nextSibling = null;
	}
	
	State<T> getChild(char c) {
		for (State<T> child = firstChild; child != null; child = child.nextSibling)
			if (child.c == c)
				return child;
		return null;
	}
	
	/**
	 * Factory method to create a child state.
	 * @param c
	 * @param parent
	 */
	protected abstract State<T> newState(char c, State<T> parent);
	
	State<T> extend(char c) {
		for (State<T> child = firstChild; child != null; child = child.nextSibling)
			if (child.c == c)
				return child;
		return newState(c, this);
	}
	
	long countStates() {
		long result = 1;
		for (State<T> child = firstChild; child != null; child = child.nextSibling)
			result += child.countStates();
		return result;
	}
	
	/**
	 * Extends this state with the specified character sequence and adds the specified entry value.
	 * @param value
	 * @param key
	 */
	protected abstract void addValue(T value, CharSequence key);

	/**
	 * Returns all values associated to this state.
	 * If this state is not the final state for an entry then the result is an empty list.
	 */
	public abstract Collection<T> getValues();

	/**
	 * Returns either this state is a final state for an entry.
	 */
	public abstract boolean hasValue();
}
