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


package org.bibliome.util.qsync.cli;

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
