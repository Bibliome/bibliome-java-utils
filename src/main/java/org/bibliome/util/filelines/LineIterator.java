package org.bibliome.util.filelines;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

public final class LineIterator implements Iterator<String> {
	private final BufferedReader reader;
	private String next;

	public LineIterator(BufferedReader reader) throws IOException {
		super();
		this.reader = reader;
		this.next = reader.readLine();
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public String next() {
		String result = next;
		try {
			next = reader.readLine();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
