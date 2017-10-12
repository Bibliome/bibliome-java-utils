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

package fr.inra.maiage.bibliome.util.pattern;

import java.util.List;
import java.util.regex.Matcher;

/**
 * A matcher finds all matches for a given pattern in a given sequence.
 * @author rbossy
 *
 * @param <T>
 */
public class SequenceMatcher<T> {
	private final int numCharsPerElt;
	private final List<T> sequence;
	private final Matcher matcher;

	SequenceMatcher(int numCharsPerElt, List<T> sequence, Matcher matcher) {
		super();
		this.numCharsPerElt = numCharsPerElt;
		this.sequence = sequence;
		this.matcher = matcher;
//		System.err.println("numCharsPerElt = " + numCharsPerElt);
	}

	/**
	 * Finds next match.
	 * @return either ther is a match.
	 */
	public boolean next() {
		return matcher.find();
	}
	
	private final int sequenceIndex(int index) {
		return index / numCharsPerElt;
	}
	
	/**
	 * Returns the index of the first object in the current match.
	 */
	public int getStartIndex() {
		return sequenceIndex(matcher.start());
	}
	
	/**
	 * Returns the index of the last object in the current match plus one.
	 */
	public int getEndIndex() {
		return sequenceIndex(matcher.end());
	}
	
	/**
	 * Returns the first object in the current match.
	 */
	public T getStartElement() {
		return sequence.get(getStartIndex());
	}
	
	/**
	 * Returns the last object in the current match.
	 */
	public T getEndElement() {
		return sequence.get(getEndIndex() - 1);
	}
	
	/**
	 * Returns the input subsequence corresponding to the current match.
	 */
	public List<T> getMatchedElements() {
		return sequence.subList(getStartIndex(), getEndIndex());
	}

	/**
	 * Returns the index of the first element of the specified group in the current match.
	 * @param group
	 */
	public int getStartIndex(int group) {
		return sequenceIndex(matcher.start(group));
	}
	
	/**
	 * Returns the index of the last element of the specified group in the current match plus one.
	 * @param group
	 */
	public int getEndIndex(int group) {
		return sequenceIndex(matcher.end(group));
	}

	/**
	 * Returns the first object in the specified group in the current match.
	 * @param group
	 */
	public T getStartElement(int group) {
		return sequence.get(getStartIndex(group));
	}
	
	/**
	 * Returns the last object in the specified group in the current match.
	 * @param group
	 */
	public T getEndElement(int group) {
		return sequence.get(getEndIndex(group) - 1);
	}
	
	/**
	 * Returns the subsequence of the input corresponding to the specified group in the current match.
	 * @param group
	 */
	public List<T> getMatchedElements(int group) {
		return sequence.subList(getStartIndex(group), getEndIndex(group));
	}

	public List<T> getSequence() {
		return sequence;
	}
}
