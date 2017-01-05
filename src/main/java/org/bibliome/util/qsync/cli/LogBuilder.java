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

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class LogBuilder {
	private String logFile = null;

	public LogBuilder() {
		super();
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public Logger getLogger() throws IOException {
		Logger logger = Logger.getLogger("qsync");
		logger.setUseParentHandlers(false);
		Handler handler = new FlushedStreamHandler(System.err, LogFormatter.INSTANCE);
		logger.addHandler(handler);
		if (logFile != null) {
			Handler fileHandler = new FileHandler(logFile);
			logger.addHandler(fileHandler);
		}
		return logger;
	}
}
