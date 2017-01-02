package org.bibliome.util.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExFilter<S extends CharSequence> implements Filter<S> {
	private final Pattern pattern;
	private final boolean match;

	public RegExFilter(Pattern pattern, boolean match) {
		super();
		this.pattern = pattern;
		this.match = match;
	}
	
	public RegExFilter(String pattern, boolean match) {
		this(Pattern.compile(pattern), match);
	}
	
	public RegExFilter(Pattern pattern) {
		this(pattern, false);
	}
	
	public RegExFilter(String pattern) {
		this(pattern, false);
	}

	@Override
	public boolean accept(S x) {
		Matcher m = pattern.matcher(x);
		if (match)
			return m.matches();
		return m.find();
	}
}
