package org.bibliome.util.taxonomy;

import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * A string cache allows to keep a single instance of a given string.
 * @author rbossy
 *
 */
class StringCache {
	private final Map<CharBuffer,String> map = new HashMap<CharBuffer,String>();

	private String get(CharBuffer key) {
		if (map.containsKey(key))
			return map.get(key);
		String value = new String(key.toString());
		map.put(key, value);
		return value;
	}

	/**
	 * Returns a canonical instance of string for the specified character sequence.
	 * @param s
	 */
	public String get(CharSequence s) {
		return get(CharBuffer.wrap(s));
	}

	/**
	 * Returns a canonical instance of string for the specified character array.
	 */
	public String get(char[] a) {
		return get(CharBuffer.wrap(a));
	}
	
	/**
	 * Returns a canonical instance of string for the specified character array slice.
	 */
	public String get(char[] a, int offset, int length) {
		return get(CharBuffer.wrap(a, offset, length));
	}
}
