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
