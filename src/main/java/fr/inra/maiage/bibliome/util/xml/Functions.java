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

package fr.inra.maiage.bibliome.util.xml;

import fr.inra.maiage.bibliome.util.Strings;

/**
 * Functions to use with the Saxon XSLT processor.
 * @author rbossy
 *
 */
public class Functions {
	/**
	 * Transforms the specified string into upper case.
	 * @param s
	 */
	public static final String upper(String s) {
		return s.toUpperCase();
	}
	
	/**
	 * Transforms the specified string into lower case.
	 * @param s
	 */
	public static final String lower(String s) {
		return s.toLowerCase();
	}
	
	/**
	 * Transforms the specified string by translating the first character into an upper case.
	 * @param s
	 */
	public static final String capital(String s) {
		if (s.isEmpty())
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	/**
	 * Replace all occurrences of a string by another.
	 * @param target
	 * @param s
	 * @param r
	 */
	public static final String replace(String target, String s, String r) {
		return target.replace(s, r);
	}
	
	/**
	 * Escape LaTeX special characters in the specified string.
	 * @param s
	 */
	public static final String escapeLatex(String s) {
		return Strings.escapeLatex(s);
	}
}
