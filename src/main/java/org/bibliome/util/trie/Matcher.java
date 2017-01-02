package org.bibliome.util.trie;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bibliome.util.Iterators;

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

	/**
	 * Terminates a search.
	 * @param pos
	 * @return the successful matches.
	 */
	public List<Match<T>> finish(int pos) {
		for (Match<T> m : candidates)
			if (m.getState().isKey() && m.isMatched())
				matches.add(m.finish(pos));
		return matches;
	}

	/**
	 * Start a new candidate.
	 * @param pos
	 */
	public void start(int pos) {
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
		prec = this.c;
		this.c = c;
		cc = control.canonize(prec, c);
		skipSearch  = control.skipSearch(prec, c);
	}

	/**
	 * Matches the specified char as if it was the next query character.
	 * @param pos
	 * @param c
	 */
	public void matchChar(int pos, char c) {
		setChar(c);
		if (control.canEnd(prec, c))
			finish(pos);
		if (control.canStart(prec, c))
			start(pos);
		match();
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
}
