package org.bibliome.util.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class OutputStreamTargetStream extends AbstractTargetStream {
	private final OutputStream os;
	private final String name;
	
	public OutputStreamTargetStream(String charset, OutputStream os, String name) {
		super(charset);
		this.os = os;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return os;
	}

	@Override
	public boolean check(Logger logger) {
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
