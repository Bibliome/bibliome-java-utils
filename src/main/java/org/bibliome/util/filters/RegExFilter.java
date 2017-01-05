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
