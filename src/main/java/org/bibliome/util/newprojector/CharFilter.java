package org.bibliome.util.newprojector;

/**
 * Filters characters.
 * Character filters are used to indicate a dictionary if a character in the target string should be matched.
 * @author rbossy
 *
 */
public interface CharFilter {
	/**
	 * Returns either the specified character should be matched.
	 * @param last
	 * @param c
	 */
	public boolean accept(int last, char c);
	
	/**
	 * Combines this character filter with the specified one.
	 * The returned filter accepts a character if both source filters accept the character.
	 * @param cf
	 */
	public CharFilter combine(CharFilter cf);
}
