package org.bibliome.util.trie;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Iterators;
import org.bibliome.util.marshall.MReference;

class State<T> implements MReference<State<T>> {
	private final int c;
	private final State<T> nextSibling;
	private MReference<State<T>> firstTransition;
	private MReference<List<T>> values;

	State(int c, State<T> nextSibling, MReference<State<T>> firstTransition, MReference<List<T>> values) {
		super();
		this.c = c;
		this.nextSibling = nextSibling;
		this.firstTransition = firstTransition;
		this.values = values;
	}
	
	State(int c, State<T> nextSibling) {
		this(c, nextSibling, null, null);
	}
	
	State(int c) {
		this(c, null);
	}
	
	State() {
		this(-1, null);
	}
	
	int getChar() {
		return c;
	}
	
	State<T> getNextSibling() {
		return nextSibling;
	}

	State<T> getFirstTransition() {
		if (firstTransition == null)
			return null;
		return firstTransition.get();
	}
	
	Iterator<State<T>> getTransitions() {
		if (firstTransition == null)
			return Iterators.emptyIterator();
		return new TransitionIterator<T>(getFirstTransition());
	}
	
	boolean isKey() {
		if (values == null)
			return false;
		return values.getPosition() != -1;
	}
	
	List<T> getValues() {
		if (values == null)
			return null;
		return values.get();
	}
	
	State<T> extend(char c) {
		for (State<T> t : Iterators.loop(getTransitions()))
			if (t.c == c)
				return t;
		State<T> t = new State<T>(c, getFirstTransition());
		firstTransition = t;
		return t;
	}
	
	private boolean ensureValues() {
		boolean result = values == null;
		if (result)
			values = new ValuesList<T>();
		return result;
	}
	
	boolean addValue(T value) {
		boolean result = ensureValues();
		values.get().add(value);
		return result;
	}
	
	boolean addValues(Collection<? extends T> values) {
		boolean result = ensureValues();
		this.values.get().addAll(values);
		return result;
	}

	@Override
	public int getPosition() {
		return -2;
	}

	@Override
	public State<T> get() {
		return this;
	}
	
	State<T> getState(char c) {
		for (State<T> t : Iterators.loop(getTransitions()))
			if (c == t.getChar())
				return t;
		return null;
	}
	
	void tree(PrintStream ps, int depth) {
		for (int i = 0; i < depth; ++i)
			ps.print("  ");
		ps.print((char) c);
		if (isKey()) {
			ps.print(' ');
			ps.print(getValues());
		}
		ps.println();
		depth++;
		for (State<T> t : Iterators.loop(getTransitions()))
			t.tree(ps, depth);
	}
}
