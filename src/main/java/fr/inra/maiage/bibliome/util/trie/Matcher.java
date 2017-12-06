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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import fr.inra.maiage.bibliome.util.Iterators;

/**
 * A matcher object allows to search keys in a trie.
 * @author rbossy
 *
 * @param <T>
 */
public class Matcher<T> {
	private final Trie<T> trie;
	private final MatchControl control;
	private final List<Match<T>> matches = new ArrayList<Match<T>>();
	private final List<Match<T>> candidates = new ArrayList<Match<T>>();
	private int prec = -1;
	@SuppressWarnings("unused")
	private char c;
	private char cc;
	private boolean skipSearch = false;

	/**
	 * Creates a matcher for the specified trie.
	 * @param trie
	 * @param control
	 */
	public Matcher(Trie<T> trie, MatchControl control) {
		super();
		this.trie = trie;
		this.control = control;
	}
	
	/**
	 * Initializes this matcher.
	 * This method should be called before searching a new query.
	 */
	public void init() {
		matches.clear();
		candidates.clear();
		prec = -1;
	}
	
	public Collection<Match<T>> setCandidates(Collection<Match<T>> candidates) {
		Collection<Match<T>> result = new ArrayList<Match<T>>(this.candidates);
		this.candidates.clear();
		this.candidates.addAll(candidates);
		return result;
	}
	
	/**
	 * Terminates a search.
	 * @param pos
	 * @return the successful matches.
	 */
	public List<Match<T>> finish(int pos) {
		for (Match<T> m : candidates) {
			//System.err.println("m = " + m);
			if (m.getState().isKey() && m.isMatched())
				matches.add(m.finish(pos));
		}
		return matches;
	}

	/**
	 * Start a new candidate.
	 * @param pos
	 */
	public void start(int pos) {
//		System.err.format("do  start: %d - %d - %c\n", pos, prec, c);
		candidates.add(new Match<T>(pos, trie.getRoot()));			
	}

	private void match() {
		ListIterator<Match<T>> lit = candidates.listIterator();
		while (lit.hasNext())
			match(lit);
	}

	private void match(ListIterator<Match<T>> lit) {
		Match<T> m = lit.next();
		State<T> state = m.getState();
		m.setMatched(false);
		match(lit, m, state);
		if (skipSearch) {
			if (m.isMatched())
				lit.add(m.spawn(state));
			else if (state == trie.getRoot())
				lit.remove();
		}
		else {
			if (!m.isMatched())
				lit.remove();
		}
	}

	private void match(ListIterator<Match<T>> lit, Match<T> m, State<T> state) {
		int mprec = state.getChar();
		for (State<T> t : Iterators.loop(state.getTransitions())) {
			char mc = (char) t.getChar();
			char mcc = control.canonize(prec, mc);
			if (cc == mcc) {
				if (m.isMatched()) {
					Match<T> n = m.spawn(t);
					n.setMatched(true);
					lit.add(n);
				}
				else {
					m.setMatched(true);
					m.setState(t);
				}
			}
			else if (control.skipMatch(mprec, mc)) {
				match(lit, m, t);
			}
		}
	}

	private void setChar(char c) {
		//prec = this.c;
		this.c = c;
		cc = control.canonize(prec, c);
		skipSearch = control.skipSearch(prec, c);
	}

	/**
	 * Matches the specified char as if it was the next query character.
	 * @param pos
	 * @param c
	 */
	public void matchChar(int pos, char c) {
		setChar(c);
		if (control.canEnd(prec, c)) {
			//System.err.format("can end: %d - %d - %c\n", pos, prec, c);
			finish(pos);
		}
		if (control.canStart(prec, c)) {
			//System.err.format("can start: %d - %d - %c\n", pos, prec, c);
			start(pos);
		}
		match();
		prec = c;
	}
	
	/**
	 * Searches for entry keys in the specified query string.
	 * @param s
	 * @param offset
	 */
	public void search(CharSequence s, int offset) {
		for (int i = 0; i < s.length(); ++i)
			matchChar(offset + i, s.charAt(i));
	}
	
	/**
	 * Searches for entry keys in the specified query string.
	 * @param s
	 */
	public void search(CharSequence s) {
		search(s, 0);
	}

	/**
	 * Returns this matcher match control object.
	 */
	public MatchControl getMatchControl() {
		return control;
	}

	/**
	 * Returns the trie from which this matcher searches.
	 */
	public Trie<T> getTrie() {
		return trie;
	}

	public List<Match<T>> getCandidates() {
		return Collections.unmodifiableList(candidates);
	}
}
