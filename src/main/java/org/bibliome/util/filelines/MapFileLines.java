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
