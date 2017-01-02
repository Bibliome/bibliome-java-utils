package org.bibliome.util.filelines;

import java.util.List;
import java.util.logging.Logger;

/**
 * File lines to load a list of entries.
 * @author rbossy
 *
 */
public class RecordFileLines extends FileLines<List<List<String>>> {
	public RecordFileLines(Logger logger) {
		super(logger);
	}

	public RecordFileLines() {
		super();
	}

	@Override
	public void processEntry(List<List<String>> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
		data.add(entry);
	}
}
