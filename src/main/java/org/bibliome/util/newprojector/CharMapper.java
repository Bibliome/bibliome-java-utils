package org.bibliome.util.newprojector;

/**
 * Transforms a target character into another.
 * Character mappers are used to make case or diacritics insensitive matches.
 * @author rbossy
 *
 */
public interface CharMapper {
	/**
	 * Transforms the specified character into another.
	 * @param last
	 * @param c
	 */
	public char map(int last, char c);
	
	/**
	 * Combine this character mapper with the specified character mapper.
	 * This method returns a character mapper that transforms a charcter with this character mapper then with the specified character mapper.
	 * @param cm
	 */
	public CharMapper combine(CharMapper cm);
}
