/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
