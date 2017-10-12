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

package fr.inra.maiage.bibliome.util;

/**
 * String concatenation utility class.
 * This class relies on a StringBuilder object.
 * However if the only concatenated characters comes from a single string object then this class toString() will avoid a double character array copy.
 * @author rbossy
 *
 */
public class StringCat implements CharSequence {
	private StringBuilder sb;
	private CharSequence cs;

	/**
	 * Creates a new string concatenation.
	 */
	public StringCat() {
		super();
	}

	@Override
	public char charAt(int index) {
		if (cs == null)
			throw new IndexOutOfBoundsException();
		return cs.charAt(index);
	}

	@Override
	public int length() {
		if (cs == null)
			return 0;
		return cs.length();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		if (cs == null)
			return "";
		return cs.subSequence(start, end);
	}

	@Override
	public boolean equals(Object other) {
		if (cs == null)
			return other == null;
		return cs.equals(other);
	}

	@Override
	public int hashCode() {
		if (cs == null)
			return 0;
		return cs.hashCode();
	}

	@Override
	public String toString() {
		if (cs == null)
			return "";
		return cs.toString();
	}

	/**
	 * Appends characters.
	 * @param a
	 */
	public void append(CharSequence a) {
		if (a == null)
			return;
		if (a.length() == 0)
			return;
		if (cs == null) {
			cs = a;
			return;
		}
		if (sb == null) {
			sb = new StringBuilder(cs.length() + a.length());
			sb.append(cs);
			cs = sb;
		}
		sb.append(a);
	}

	public void append(Object obj) {
		if (obj == null)
			return;
		if (obj instanceof CharSequence)
			append((CharSequence) obj);
		else {
			if (sb == null) {
				sb = new StringBuilder();
				if (cs != null)
					sb.append(cs);
				cs = sb;
			}
			sb.append(obj);
		}
	}
	
	/**
	 * Resets this string cache.
	 */
	public void clear() {
		sb = null;
		cs = null;
	}
	
	/**
	 * Returns true iff this string cache is empty, nothing has been appended.
	 */
	public boolean isEmpty() {
		if (sb != null)
			return sb.length() == 0;
		if (cs != null)
			return cs.length() == 0;
		return true;
	}
}
