package org.bibliome.util.streams;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;

public abstract class AbstractTargetStream implements TargetStream {
	protected final String charset;
	
	protected AbstractTargetStream(String charset) {
		super();
		this.charset = charset;
	}

	@Override
	public Writer getWriter() throws IOException {
		return new OutputStreamWriter(getOutputStream(), charset);
	}

	@Override
	public PrintStream getPrintStream() throws IOException {
		return new PrintStream(getOutputStream(), true, charset);
	}

	@Override
	public BufferedWriter getBufferedWriter() throws IOException {
		return new BufferedWriter(getWriter());
	}
}
