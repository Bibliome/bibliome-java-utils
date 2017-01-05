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
