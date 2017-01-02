package org.bibliome.util.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.bibliome.util.Iterators;

public abstract class AbstractSingleSourceStream extends AbstractSourceStream {
	protected final CompressionFilter compressionFilter;
	
	protected AbstractSingleSourceStream(String charset, CompressionFilter compressionFilter) {
		super(charset);
		this.compressionFilter = compressionFilter;
	}
	
	public AbstractSingleSourceStream(String charset) {
		this(charset, CompressionFilter.NONE);
	}

	@Override
	public Iterator<Reader> getReaders() throws IOException {
		return Iterators.singletonIterator(getReader());
	}

	@Override
	public Iterator<InputStream> getInputStreams() throws IOException {
		return Iterators.singletonIterator(getInputStream());
	}

	@Override
	public Iterator<BufferedReader> getBufferedReaders() throws IOException {
		return Iterators.singletonIterator(getBufferedReader());
	}
}
