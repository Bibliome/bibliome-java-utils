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
