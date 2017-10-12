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

package fr.inra.maiage.bibliome.util.taxonomy;

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
