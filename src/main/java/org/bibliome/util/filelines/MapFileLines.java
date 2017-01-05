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
import java.util.Map;

/**
 * File lines to load map entries.
 * The first column is the key and the second is the value.
 * @author rbossy
 *
 */
public class MapFileLines extends FileLines<Map<String,String>> {
	public MapFileLines(TabularFormat format) {
		super(format);
		format.setColumnLimit(2);
		format.setSkipBlank(true);
		format.setSkipEmpty(true);
		format.setNullifyEmpty(false);
		format.setTrimColumns(true);
		format.setSeparator('\t');
	}
	
	@Override
	public void processEntry(Map<String,String> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
		if (entry.size() == 1)
			data.put(entry.get(0), entry.get(0));
		else
			data.put(entry.get(0), entry.get(1));
	}
}
