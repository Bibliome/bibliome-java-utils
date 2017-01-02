package org.bibliome.util.trie;

import java.util.List;

/**
 * An instance of this class represents a match on a given trie.
 * @author rbossy
 *
 * @param <T>
 */
public class Match<T> {
	private final int start;
	private final int end;
	private State<T> state;
	private boolean matched = false;
	
	private Match(int start, int end, State<T> state) {
		super();
		if (state == null)
			throw new NullPointerException();
		this.start = start;
		this.end = end;
		this.state = state;
	}
	
	Match(int start, State<T> state) {
		this(start, -1, state);
	}

	Match<T> spawn(State<T> state) {
		return new Match<T>(start, state);
	}
	
	/**
	 * Returns the start position of this match.
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Returns the end position of this match.
	 */
	public int getEnd() {
		return end;
	}
	
	/**
	 * Returns the values associated to this match.
	 */
	public List<T> getValues() {
		return state.getValues();
	}
	
	Match<T> finish(int end) {
		return new Match<T>(start, end, state);
	}
	
	State<T> getState() {
		return state;
	}
	
	void setState(State<T> state) {
		this.state = state;
	}

	boolean isMatched() {
		return matched;
	}

	void setMatched(boolean matched) {
		this.matched = matched;
	}
}
