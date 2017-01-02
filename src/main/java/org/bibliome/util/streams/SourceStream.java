package org.bibliome.util.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;

import org.bibliome.util.Checkable;

public interface SourceStream extends Checkable {
	String getStreamName(Object stream);
	Collection<String> getStreamNames();
	Iterator<InputStream> getInputStreams() throws IOException;
	InputStream getInputStream() throws IOException;
	Iterator<Reader> getReaders() throws IOException;
	Reader getReader() throws IOException;
	Iterator<BufferedReader> getBufferedReaders() throws IOException;
	BufferedReader getBufferedReader() throws IOException;
	String getCharset();
}
