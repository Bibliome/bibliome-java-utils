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
