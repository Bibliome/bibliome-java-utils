package org.bibliome.util.streams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

public class ResourceSourceStream extends AbstractSingleSourceStream {
	private final String name;
	private final URL url;

	public ResourceSourceStream(String charset, CompressionFilter compressionFilter, String name) {
		super(charset, compressionFilter);
		this.name = name;
		Class<?> klass = getClass();
		ClassLoader classLoader = klass.getClassLoader();
		url = classLoader.getResource(name);
		if (url == null) {
			throw new RuntimeException("resource " + name + " not found");
		}
	}

	@Override
	public InputStream getInputStream() throws IOException {
		InputStream result = compressionFilter.getInputStream(url.openStream());
		setStreamName(result, name);
		return result;
	}

	@Override
	public boolean check(Logger logger) {
		if (url != null)
			return true;
		logger.severe("resource " + name + " is not available");
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Collection<String> getStreamNames() {
		return Collections.singleton(url.toString());
	}
}
