package org.bibliome.util.trie;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class TransitionIterator<T> implements Iterator<State<T>> {
	private State<T> next;

	public TransitionIterator(State<T> next) {
		super();
		this.next = next;
	}

	public TransitionIterator() {
		this(null);
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public State<T> next() {
		if (next == null)
			throw new NoSuchElementException();
		State<T> result = next;
		next = next.getNextSibling();
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	Iterator<State<T>> setState(State<T> state) {
		next = state;
		return this;
	}
}
