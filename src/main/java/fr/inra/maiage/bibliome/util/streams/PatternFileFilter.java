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

package fr.inra.maiage.bibliome.util.streams;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternFileFilter implements FileFilter {
	private final Pattern pattern;
	private final boolean fullNameFilter;
	private final boolean wholeMatch;

	public PatternFileFilter() {
		this(null, false, false);
	}

	public PatternFileFilter(Pattern pattern, boolean fullNameFilter, boolean wholeMatch) {
		super();
		this.pattern = pattern;
		this.fullNameFilter = fullNameFilter;
		this.wholeMatch = wholeMatch;
	}

	@Override
	public boolean accept(File file) {
		if (!file.canRead()) {
			return false;
		}
		if (pattern == null) {
			return true;
		}
		String s = fullNameFilter ? file.getAbsolutePath() : file.getName();
		Matcher m = pattern.matcher(s);
		if (wholeMatch) {
			return m.matches();
		}
		return m.find();
	}

	public Pattern getPattern() {
		return pattern;
	}

	public boolean isFullNameFilter() {
		return fullNameFilter;
	}

	public boolean isWholeMatch() {
		return wholeMatch;
	}
}
