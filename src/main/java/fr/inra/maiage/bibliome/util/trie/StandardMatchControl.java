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

package fr.inra.maiage.bibliome.util.trie;

import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import fr.inra.maiage.bibliome.util.Strings;

/**
 * MatchControl implementation with most current options.
 * @author rbossy
 *
 */
public class StandardMatchControl implements MatchControl {
	private boolean neverEnd = false;
	private boolean endWordBoundary = false;
	private boolean neverStart = false;
	private boolean startWordBoundary = false;
	private boolean ignoreDiacritics = false;
	private boolean caseInsensitive = false;
	private boolean wordStartCaseInsensitive = false;
	private boolean allUpperCaseInsensitive = false;
	private boolean matchStartCaseInsensitive = false;
	private boolean allowJoined = false;
	private boolean joinDash = false;
	private boolean skipWhitespace = false;
	private boolean skipConsecutiveWhitespaces = false;

	/**
	 * Creates a new standard match control with default values.
	 */
	public StandardMatchControl() {
		super();
	}
	
	/**
	 * Creates a copy of the specified standard match control.
	 * @param control
	 */
	public StandardMatchControl(StandardMatchControl control) {
		super();
		neverEnd = control.neverEnd;
		endWordBoundary = control.endWordBoundary;
		neverStart = control.neverStart;
		startWordBoundary = control.startWordBoundary;
		ignoreDiacritics = control.ignoreDiacritics;
		caseInsensitive = control.caseInsensitive;
		wordStartCaseInsensitive = control.wordStartCaseInsensitive;
		allUpperCaseInsensitive = control.allUpperCaseInsensitive;
		matchStartCaseInsensitive = control.matchStartCaseInsensitive;
		allowJoined = control.allowJoined;
		joinDash = control.joinDash;
		skipWhitespace = control.skipWhitespace;
		skipConsecutiveWhitespaces = control.skipConsecutiveWhitespaces;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("StandardMatchControl(");
		List<String> options = new ArrayList<String>(12);
		addOption(options, neverEnd, "neverEnd");
		addOption(options, endWordBoundary, "endWordBoundary");
		addOption(options, neverStart, "neverStart");
		addOption(options, startWordBoundary, "startWordBoundary");
		addOption(options, ignoreDiacritics, "ignoreDiacritics");
		addOption(options, caseInsensitive, "caseInsensitive");
		addOption(options, wordStartCaseInsensitive, "wordStartCaseInsensitive");
		addOption(options, matchStartCaseInsensitive, "matchStartCaseInsensitive");
		addOption(options, allUpperCaseInsensitive, "allUpperCaseInsensitive");
		addOption(options, allowJoined, "allowJoined");
		addOption(options, joinDash, "joinDash");
		addOption(options, skipWhitespace, "skipWhitespace");
		addOption(options, skipConsecutiveWhitespaces, "skipConsecutiveWhitespaces");
		try {
			Strings.join(sb, options, ", ");
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		sb.append(')');
		return sb.toString();
	}
	
	private static void addOption(List<String> options, boolean value, String name) {
		if (value)
			options.add(name);
	}
	
	@Override
	public boolean canEnd(int prev, char c) {
		if (neverEnd)
			return false;
		if (prev == -1)
			return false;
		if (endWordBoundary)
//			return Character.isLetterOrDigit(prev) && !Character.isLetterOrDigit(c);
			return !Character.isLetterOrDigit(c);
		return true;
	}

	@Override
	public boolean canStart(int prev, char c) {
		if (neverStart)
			return false;
		if (prev == -1)
			return true;
		if (startWordBoundary)
			return Character.isLetterOrDigit(c) && !Character.isLetterOrDigit(prev);
		return true;
	}

	@Override
	public char canonize(int prev, char c) {
		if (ignoreDiacritics)
			c = removeDiacritics(c);
		if (foldCase(prev, c))
			return Character.toUpperCase(c);
		return c;
	}
	
	private static char removeDiacritics(char c) {
		StringBuilder sb = new StringBuilder();
		sb.append(c);
		String norm = Normalizer.normalize(sb, Normalizer.Form.NFD);
		return norm.charAt(0);		
	}

	private boolean foldCase(int prev, char c) {
		if (!Character.isLetter(c))
			return false;
		if (Character.isUpperCase(c))
			return false;
		if (caseInsensitive)
			return true;
		if (allUpperCaseInsensitive && Character.isUpperCase(prev))
			return true;
		if (wordStartCaseInsensitive)
			return prev == -1 || !Character.isLetterOrDigit(prev);
		if (matchStartCaseInsensitive)
			return prev == -1;
		return false;
	}

	@Override
	public boolean skipMatch(int mprev, char mc) {
		if (ignoreDiacritics && isDiacritic(mc))
			return true;
		if (allowJoined) {
			if (Character.isWhitespace(mc))
				return true;
			if (joinDash && mc == '-')
				return true;
		}
		return false;
	}

	private static boolean isDiacritic(char mc) {
		UnicodeBlock block = UnicodeBlock.of(mc);
		return UnicodeBlock.COMBINING_DIACRITICAL_MARKS.equals(block) || UnicodeBlock.COMBINING_DIACRITICAL_MARKS_SUPPLEMENT.equals(block);
	}

	@Override
	public boolean skipSearch(int prev, char c) {
		if (Character.isWhitespace(c)) {
			if (skipWhitespace)
				return true;
			if (skipConsecutiveWhitespaces && Character.isWhitespace(prev))
				return true;
		}
		return false;
	}

	/**
	 * Returns either ending is disallowed during search.
	 */
	public boolean isNeverEnd() {
		return neverEnd;
	}

	/**
	 * Returns either ending is allowed only on word boundaries.
	 */
	public boolean isEndWordBoundary() {
		return endWordBoundary;
	}

	/**
	 * Returns either starting is disallowed during search.
	 */
	public boolean isNeverStart() {
		return neverStart;
	}

	/**
	 * Returns either ending is allowed only on word boundaries.
	 */
	public boolean isStartWordBoundary() {
		return startWordBoundary;
	}

	/**
	 * Returns either the search will ignore diacritics.
	 */
	public boolean isIgnoreDiacritics() {
		return ignoreDiacritics;
	}

	/**
	 * Returns either the search will ignore case.
	 */
	public boolean isCaseInsensitive() {
		return caseInsensitive;
	}

	/**
	 * Returns either the search will ignore case on the first character after each word boundary.
	 */
	public boolean isWordStartCaseInsensitive() {
		return wordStartCaseInsensitive;
	}

	/**
	 * Returns either the search will ignore case on the first character of the key.
	 */
	public boolean isMatchStartCaseInsensitive() {
		return matchStartCaseInsensitive;
	}

	/**
	 * Returns either whitespace states can be skipped.
	 */
	public boolean isAllowJoined() {
		return allowJoined;
	}

	/**
	 * Returns either dash ('-') states can be skipped.
	 */
	public boolean isJoinDash() {
		return joinDash;
	}

	/**
	 * Returns either query whitespace can be skipped.
	 */
	public boolean isSkipWhitespace() {
		return skipWhitespace;
	}

	/**
	 * Returns either query consecutive whitespace can be skipped.
	 */
	public boolean isSkipConsecutiveWhitespaces() {
		return skipConsecutiveWhitespaces;
	}

	/**
	 * Returns either the search will ignore case if the query is upper case.
	 */
	public boolean isAllUpperCaseInsensitive() {
		return allUpperCaseInsensitive;
	}

	public void setAllUpperCaseInsensitive(boolean allUpperCaseInsensitive) {
		this.allUpperCaseInsensitive = allUpperCaseInsensitive;
	}

	public void setNeverEnd(boolean neverEnd) {
		this.neverEnd = neverEnd;
	}

	public void setEndWordBoundary(boolean endWordBoundary) {
		this.endWordBoundary = endWordBoundary;
	}

	public void setNeverStart(boolean neverStart) {
		this.neverStart = neverStart;
	}

	public void setStartWordBoundary(boolean startWordBoundary) {
		this.startWordBoundary = startWordBoundary;
	}

	public void setIgnoreDiacritics(boolean ignoreDiacritics) {
		this.ignoreDiacritics = ignoreDiacritics;
	}

	public void setCaseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}

	public void setWordStartCaseInsensitive(boolean wordStartCaseInsensitive) {
		this.wordStartCaseInsensitive = wordStartCaseInsensitive;
	}

	public void setMatchStartCaseInsensitive(boolean matchStartCaseInsensitive) {
		this.matchStartCaseInsensitive = matchStartCaseInsensitive;
	}

	public void setAllowJoined(boolean allowJoined) {
		this.allowJoined = allowJoined;
	}

	public void setJoinDash(boolean joinDash) {
		this.joinDash = joinDash;
	}

	public void setSkipWhitespace(boolean skipWhitespace) {
		this.skipWhitespace = skipWhitespace;
	}

	public void setSkipConsecutiveWhitespaces(boolean skipConsecutiveWhitespaces) {
		this.skipConsecutiveWhitespaces = skipConsecutiveWhitespaces;
	}
	
	public void setWordBoundary(boolean wordBoundary) {
		setStartWordBoundary(wordBoundary);
		setEndWordBoundary(wordBoundary);
	}
}
