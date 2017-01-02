package org.bibliome.util.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;

import org.bibliome.util.Iterators;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;

public abstract class AbstractMultipleSourceStream extends AbstractSourceStream {
	public AbstractMultipleSourceStream(String charset) {
		super(charset);
	}
	
	private final Mapper<InputStream,Reader> inputStreamToReader = new Mapper<InputStream,Reader>() {
		@Override
		public Reader map(InputStream x) {
			try {
				Reader result = new InputStreamReader(x, charset);
				setStreamName(result, getStreamName(x));
				return result;
			}
			catch (UnsupportedEncodingException uee) {
				throw new RuntimeException(uee);
			}
		}
	};
	
	private final Mapper<Reader,BufferedReader> readerToBufferedReader = new Mapper<Reader,BufferedReader>() {
		@Override
		public BufferedReader map(Reader x) {
			BufferedReader result = new BufferedReader(x);
			setStreamName(result, getStreamName(x));
			return result;
		}
	};
	
	@Override
	public Iterator<Reader> getReaders() throws IOException {
		return Mappers.apply(inputStreamToReader, getInputStreams());
	}

	@Override
	public Iterator<BufferedReader> getBufferedReaders() throws IOException {
		return Mappers.apply(readerToBufferedReader, getReaders());
	}

	@Override
	public InputStream getInputStream() throws IOException {
		Enumeration<InputStream> e = Iterators.getEnumeration(getInputStreams());
		InputStream result = new SequenceInputStream(e);
		setStreamName(result, getCollectiveName());
		return result;
	}
	
	protected abstract String getCollectiveName();
}
