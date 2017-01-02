package org.bibliome.util.filelines;

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
