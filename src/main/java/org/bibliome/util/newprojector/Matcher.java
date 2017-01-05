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

package org.bibliome.util.newprojector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Object used to perform a search.
 * @author rbossy
 *
 * @param <T>
 */
public class Matcher<T> {
	private final List<Match<T>> pending = new ArrayList<Match<T>>();
	private final List<Match<T>> finished = new ArrayList<Match<T>>();
	private final boolean charPos;
	private final CharFilter filter;
	private final CharMapper mapper;
	private final CharFilter start;
	private final CharFilter end;
	private Dictionary<T> dict = null;
	private int position = -1;
	private int last = -1;
	private final Set<State<T>> visitedStates;
	
	private Matcher(boolean charPos, boolean countVisitedStates, CharFilter filter, CharMapper mapper, CharFilter start, CharFilter end) {
		super();
		this.charPos = charPos;
		this.filter = filter;
		this.mapper = mapper;
		this.start = start;
		this.end = end;
		visitedStates = countVisitedStates ? new HashSet<State<T>>() : null;
	}

	/**
	 * Creates a matcher.
	 * @param charPos
	 * @param dict
	 * @param start
	 * @param end
	 */
	public Matcher(boolean charPos, boolean countVisitedStates, Dictionary<T> dict, CharFilter start, CharFilter end) {
		this(charPos, countVisitedStates, dict.getFilter(), dict.getMapper(), start, end);
		this.dict = dict;
	}

	/**
	 * Creates a matcher.
	 * @param dict
	 * @param start
	 * @param end
	 */
	public Matcher(Dictionary<T> dict, CharFilter start, CharFilter end) {
		this(true, false, dict, start, end);
	}
	
	/**
	 * Resets this matcher.
	 */
	public void reset() {
		pending.clear();
		finished.clear();
		position = -1;
		last = -1;
	}
	
	/**
	 * Starts a new match at the current position.
	 */
	public void startMatch() {
		Match<T> m = dict.newMatch();
		m.setStart(position);
		pending.add(m);
	}
	
	/**
	 * Ends all matches at the current position.
	 */
	public void endMatches() {
		for (Match<T> m : pending) {
			if (m.getState().hasValue()) {
				m.setEnd(position + (charPos ? 1 : 0));
				finished.add(m.copy());
			}
		}
	}
	
	/**
	 * Sets the current position.
	 * @param position
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Matches the specified character.
	 * @param c
	 */
	public void matchChar(char c) {
		if (end.accept(last, c))
			endMatches();
		if (charPos)
			position++;
		if (start.accept(last, c))
			startMatch();
		if (!filter.accept(last, c)) {
			last = c;
			return;
		}
		ListIterator<Match<T>> lit = pending.listIterator();
		while (lit.hasNext()) {
			Match<T> m = lit.next();
			char mc = mapper.map(last, c);
			State<T> s = m.getState().getChild(mc);
			if (s == null)
				lit.remove();
			else {
				if (visitedStates != null)
					visitedStates.add(s);
				m.setState(s);
			}
		}
		last = c;
	}
	
	public int getVisitedStatesCount() {
		if (visitedStates == null)
			return -1;
		return visitedStates.size();
	}
	
	/**
	 * Returns the successful matches.
	 */
	public List<Match<T>> getMatches() {
		return Collections.unmodifiableList(finished);
	}
	
	/**
	 * Returns the dictionary.
	 */
	public Dictionary<T> getDictionary() {
		return dict;
	}
	
	/**
	 * Sets the dictionary.
	 * @param dict
	 */
	public void setDictionary(Dictionary<T> dict) {
		this.dict = dict;
	}
	
	/**
	 * Matches the specified character sequence.
	 * @param key
	 */
	public void match(CharSequence key) {
		dict.match(this, key);
	}
}
