package org.bibliome.util.streams;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import org.bibliome.util.files.InputFile;

public class FileSourceStream extends AbstractSingleSourceStream {
	private final InputFile file;

	public FileSourceStream(String charset, CompressionFilter compressionFilter, InputFile file) {
		super(charset, compressionFilter);
		this.file = file;
	}

	public FileSourceStream(String charset, CompressionFilter compressionFilter, String file) {
		super(charset, compressionFilter);
		this.file = new InputFile(file);
	}

	public FileSourceStream(String charset, InputFile file) {
		super(charset);
		this.file = file;
	}

	public FileSourceStream(String charset, String file) {
		super(charset);
		this.file = new InputFile(file);
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		InputStream result = compressionFilter.getInputStream(new FileInputStream(file));
		setStreamName(result, file.getAbsolutePath());
		return result;
	}

	@Override
	public boolean check(Logger logger) {
		return file.check(logger);
	}

	@Override
	public String toString() {
		return file.toString();
	}

	@Override
	public Collection<String> getStreamNames() {
		return Collections.singleton(file.getAbsolutePath());
	}
}
