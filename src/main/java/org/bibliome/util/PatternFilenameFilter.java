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
