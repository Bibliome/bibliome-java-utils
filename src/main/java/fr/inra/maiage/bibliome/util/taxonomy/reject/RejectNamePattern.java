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

package fr.inra.maiage.bibliome.util.taxonomy.reject;

import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.util.taxonomy.Name;

/**
 * Reject names that matches a regular expression.
 * @author rbossy
 *
 */
public class RejectNamePattern implements RejectName {
	private final Pattern pattern;

	/**
	 * Creates a name reject based on the specified pattern.
	 * @param pattern
	 */
	public RejectNamePattern(Pattern pattern) {
		super();
		this.pattern = pattern;
	}

	@Override
	public boolean reject(String taxid, Name name) {
		return pattern.matcher(name.name).matches();
	}

	@Override
	public RejectName simplify() {
		return this;
	}
}
