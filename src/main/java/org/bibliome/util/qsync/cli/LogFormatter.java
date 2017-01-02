/*
  This file is part of AlvisNLP software.
  Copyright Institut National de la Recherche Agronomique, 2009.
  AlvisNLP is a result of Quaero project and its use must respect the rules of the Quaero Project Consortium Agreement.
 */

package org.bibliome.util.qsync.cli;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * AlvisNLP-specific log formatter.
 */
class LogFormatter extends Formatter {
	static final LogFormatter INSTANCE = new LogFormatter();
	
    /** The df. */
    private final DateFormat df = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");

    /**
     * Constructs a new AlvisNLPLogFormatter object.
     */
    private LogFormatter() {
        super();
    }

    /**
     * Returns a string representation of the specified record.
     * 
     * @param rec
     *            the rec
     * 
     * @return a string representation of the specified record
     */
    @Override
    public String format(LogRecord rec) {
        StringBuilder sb = new StringBuilder(df.format(new Date(rec.getMillis())));
        sb.append('[');
        sb.append(rec.getLoggerName());
        sb.append("] ");
        Level lvl = rec.getLevel();
        if ((lvl == Level.WARNING) || (lvl == Level.SEVERE)) {
            sb.append(lvl);
            sb.append(' ');
        }
        sb.append(rec.getMessage());
        for (Throwable cause = rec.getThrown(); cause != null; cause = cause.getCause()) {
            sb.append("\n\n### error type:\n###     ");
            sb.append(cause.getClass().getCanonicalName());
            sb.append("\n###\n### error message:\n###     ");
            sb.append(cause.getMessage());
            sb.append("\n###\n### stack trace:\n");
            for (StackTraceElement elt : cause.getStackTrace()) {
                sb.append("###     ");
                sb.append(elt.toString());
                sb.append('\n');
            }
        }
        sb.append("\n");
        return sb.toString();
    }
}
