package org.bibliome.util.streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import org.bibliome.util.files.ExecutableFile;

public class PipeSourceStream extends AbstractSingleSourceStream {
	private final String[] commandArray;
	
	public PipeSourceStream(String charset, CompressionFilter compressionFilter, String[] commandArray) {
		super(charset, compressionFilter);
		this.commandArray = commandArray;
	}

	public PipeSourceStream(String charset, String[] commandArray) {
		super(charset);
		this.commandArray = commandArray;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(commandArray);
		try (InputStream result = process.getInputStream()) {
			setStreamName(result, commandArray[0]);
			int retval = process.waitFor();
			if (retval != 0)
				throw new IOException("command " + commandArray[0] + " failed, return value = " + retval);
			return result;
		}
		catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean check(Logger logger) {
		ExecutableFile command = new ExecutableFile(commandArray[0]);
		return command.check(logger);
	}

	@Override
	public Collection<String> getStreamNames() {
		return Collections.singleton(commandArray[0]);
	}
}
