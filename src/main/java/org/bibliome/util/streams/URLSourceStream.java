package org.bibliome.util.streams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

public class URLSourceStream extends AbstractSingleSourceStream {
	private final URL url;

	public URLSourceStream(String charset, CompressionFilter compressionFilter, URL url) {
		super(charset, compressionFilter);
		this.url = url;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		InputStream result = compressionFilter.getInputStream(url.openStream());
		setStreamName(result, url.toString());
		return result;
	}

	@Override
	public boolean check(Logger logger) {
		try {
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
			return true;
		}
		catch (IOException ioe) {
			logger.severe(ioe.getMessage());
			return false;
		}
	}

	@Override
	public String toString() {
		return url.toString();
	}

	@Override
	public Collection<String> getStreamNames() {
		return Collections.singleton(url.toString());
	}
}
