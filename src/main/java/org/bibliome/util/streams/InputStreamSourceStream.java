package org.bibliome.util.streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

public class InputStreamSourceStream extends AbstractSingleSourceStream {
	private final InputStream is;
	private final String name;
	
	public InputStreamSourceStream(String charset, CompressionFilter compressionFilter, InputStream is, String name) {
		super(charset, compressionFilter);
		this.is = is;
		this.name = name;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		InputStream result = compressionFilter.getInputStream(is);
		setStreamName(result, name);
		return result;
	}

	@Override
	public boolean check(Logger logger) {
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Collection<String> getStreamNames() {
		return Collections.singleton(name);
	}
}
