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

package org.bibliome.util.filelines;

import java.util.List;
import java.util.logging.Logger;

import org.bibliome.util.EquivalenceSets;

/**
 * File lines for loading equivalence sets.
 * All columns of an entry are considered to be equivalent.
 * @author rbossy
 *
 */
public class EquivFileLines extends FileLines<EquivalenceSets<String>> {
    public EquivFileLines(TabularFormat format, Logger logger) {
		super(format, logger);
	}

	public EquivFileLines(TabularFormat format) {
		super(format);
	}

	public EquivFileLines() {
		super();
	}

	public EquivFileLines(Logger logger) {
		super(logger);
	}

	@Override
    public void processEntry(EquivalenceSets<String> data, int lineno, List<String> entry) {
    	String prev = null;
    	for (String e : entry) {
    		if (e == null)
    			continue;
    		data.setEquivalent(prev, e);
    		prev = e;
    	}
    }
}
