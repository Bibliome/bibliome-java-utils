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

package fr.inra.maiage.bibliome.util.trie;

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

	@Override
	public String toString() {
		return "Match [start=" + start + ", end=" + end + ", state=" + state + " [" + ((char) state.getChar()) + "], matched=" + matched + "]";
	}
}
