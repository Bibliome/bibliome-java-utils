package org.bibliome.util.streams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bibliome.util.files.OutputFile;

public class FileTargetStream extends AbstractTargetStream {
	private final OutputFile file;
	private final boolean append;

	public FileTargetStream(String charset, OutputFile file, boolean append) {
		super(charset);
		this.file = file;
		this.append = append;
	}
	
	public FileTargetStream(String charset, OutputFile file) {
		this(charset, file, false);
	}

	public FileTargetStream(String charset, String file, boolean append) {
		this(charset, new OutputFile(file), append);
	}

	public FileTargetStream(String charset, String file) {
		this(charset, new OutputFile(file));
	}

	@Override
	public String getName() {
		return file.getAbsolutePath();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		File dir = file.getParentFile();
		if (dir != null)
			dir.mkdirs();
		return new FileOutputStream(file, append);
	}

	@Override
	public boolean check(Logger logger) {
		return file.check(logger);
	}

	@Override
	public String toString() {
		return file.toString();
	}
}
