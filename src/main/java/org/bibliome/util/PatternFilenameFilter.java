package org.bibliome.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * Filename filter.
 * To be used with {@link File#listFiles(FilenameFilter)}.
 * @author rbossy
 *
 */
public class PatternFilenameFilter implements FilenameFilter {
	private final Pattern pattern;
	
	public PatternFilenameFilter(Pattern pattern) {
		super();
		this.pattern = pattern;
	}

	public PatternFilenameFilter(String pattern) {
		this(Pattern.compile(pattern));
	}

	@Override
	public boolean accept(File dir, String name) {
		return pattern.matcher(name).find();
	}
}
