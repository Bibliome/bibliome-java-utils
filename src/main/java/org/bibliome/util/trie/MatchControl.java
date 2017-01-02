package org.bibliome.util.trie;

/**
 * MatchControl instances control how trie keys are searched in strings.
 * @author rbossy
 *
 */
public interface MatchControl {
	/**
	 * Returns either a match can end at the specified character.
	 * @param prev
	 * @param c
	 */
	boolean canEnd(int prev, char c);

	/**
	 * Returns either a match can start at the specified character.
	 * @param prev
	 * @param c
	 */
	boolean canStart(int prev, char c);
	
	/**
	 * Returns the character that must be effectively matched instead of the specified character.
	 * Both query and trie characters will be submitted to this cononization.
	 * @param prev
	 * @param c
	 */
	char canonize(int prev, char c);
	
	/**
	 * Returns either the specified character can be skipped if the match fails.
	 * Only characters of the trie will be submitted to this filter.
	 * @param mprev
	 * @param mc
	 */
	boolean skipMatch(int mprev, char mc);
	
	/**
	 * Returns either the specified character can be skipped if the match fails.
	 * Only characters of the query will be submitted to this filter.
	 * @param prev
	 * @param c
	 */
	boolean skipSearch(int prev, char c);
}
