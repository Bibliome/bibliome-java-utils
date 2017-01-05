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

package org.bibliome.util.taxonomy;

import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.TabularFormat;

abstract class DmpFileLines<T> extends FileLines<T> {
	public static final String CHARSET = "ISO-8859-1";
	
	DmpFileLines(int numColumns) {
		super();
		TabularFormat format = getFormat();
		format.setNumColumns(numColumns);
		format.setNullifyEmpty(false);
		format.setSeparator('|');
		format.setSkipBlank(true);
		format.setSkipEmpty(true);
		format.setStrictColumnNumber(true);
		format.setTrimColumns(true);
	}
}
