package org.bibliome.util.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternFilter<T extends CharSequence> implements Filter<T> {
	private final Pattern pattern;
	private final boolean wholeMatch;
	
	public PatternFilter(Pattern pattern, boolean wholeMatch) {
		super();
		this.pattern = pattern;
		this.wholeMatch = wholeMatch;
	}
	
	public PatternFilter(Pattern pattern) {
		this(pattern, false);
	}

	public PatternFilter(String pattern, boolean wholeMatch) {
		this(Pattern.compile(pattern), wholeMatch);
	}

	public PatternFilter(String pattern) {
		this(Pattern.compile(pattern));
	}

	@Override
	public boolean accept(T x) {
		Matcher m = pattern.matcher(x);
		if (wholeMatch)
			return m.matches();
		return m.find();
	}
}
