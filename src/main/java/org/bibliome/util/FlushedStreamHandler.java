/*
  This file is part of AlvisNLP software.
  Copyright Institut National de la Recherche Agronomique, 2009.
  AlvisNLP is a result of Quaero project and its use must respect the rules of the Quaero Project Consortium Agreement.
 */

package org.bibliome.util;

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * Stream handler for logging.
 * The associated stream is automatically flushed after each log record publication.
 */
public class FlushedStreamHandler extends StreamHandler {
	/**
	 * Creates a new flushed stream handler associated to the standard output and the default formatter.
	 */
    public FlushedStreamHandler() {
		super();
	}

    /**
     * Creates a new flushed stream handler associated with the specified stream and formatter.
     * @param out
     * @param formatter
     */
	public FlushedStreamHandler(OutputStream out, Formatter formatter) {
		super(out, formatter);
	}

    @Override
	public void publish(LogRecord record) {
        super.publish(record);
        flush();
    }
}
