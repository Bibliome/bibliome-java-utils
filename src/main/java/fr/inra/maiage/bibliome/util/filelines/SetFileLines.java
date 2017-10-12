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

package fr.inra.maiage.bibliome.util.filelines;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * File lines to load a set of strings.
 * Only the first column of each entry is added to the set.
 * @author rbossy
 *
 */
public class SetFileLines extends FileLines<Set<String>> {
    public SetFileLines(TabularFormat format, Logger logger) {
		super(format, logger);
	}

	public SetFileLines(TabularFormat format) {
		super(format);
	}

	@Override
    public void processEntry(Set<String> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
        data.add(entry.get(0));
    }
}
