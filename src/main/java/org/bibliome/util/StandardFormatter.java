package org.bibliome.util;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Standard log formatter.
 * This formatter just outputs the message, the level is prepended if it is equal or higher than WARNING.
 * @author rbossy
 *
 */
public class StandardFormatter extends Formatter {
	@Override
	public String format(LogRecord record) {
		StringCat strcat = new StringCat();
		if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
			strcat.append(record.getLevel().getName());
			strcat.append(": ");
		}
		strcat.append(record.getMessage());
		strcat.append("\n");
		return strcat.toString();
	}
}
