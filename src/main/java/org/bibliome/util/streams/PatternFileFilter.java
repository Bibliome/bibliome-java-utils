package org.bibliome.util.streams;

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
