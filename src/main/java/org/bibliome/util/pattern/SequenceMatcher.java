package org.bibliome.util.pattern;

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
