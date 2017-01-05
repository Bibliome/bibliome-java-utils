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
