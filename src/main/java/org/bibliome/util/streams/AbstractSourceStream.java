package org.bibliome.util.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class AbstractSourceStream implements SourceStream {
	private static final Map<Object,String> streamNames = new WeakHashMap<Object,String>();

	protected final String charset;
	
	protected AbstractSourceStream(String charset) {
		super();
		this.charset = charset;
	}

	@Override
	public String getStreamName(Object stream) {
		String result = streamNames.get(stream);
		if (result == null)
			throw new IllegalArgumentException();
		return result;
	}
	
	protected static void setStreamName(Object stream, String name) {
		streamNames.put(stream, name);
	}

	@Override
	public Reader getReader() throws IOException {
		InputStream is = getInputStream();
		Reader result = new InputStreamReader(is, charset);
		setStreamName(result, getStreamName(is));
		return result;
	}

	@Override
	public BufferedReader getBufferedReader() throws IOException {
		Reader r = getReader();
		BufferedReader result = new BufferedReader(r);
		setStreamName(result, getStreamName(r));
		return result;
	}

	@Override
	public String getCharset() {
		return charset;
	}
}
