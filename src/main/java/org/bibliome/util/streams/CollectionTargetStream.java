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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import org.bibliome.util.Strings;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;

public class CollectionTargetStream extends AbstractTargetStream {
	private final Collection<TargetStream> collection;

	public CollectionTargetStream(String charset, Collection<TargetStream> collection) {
		super(charset);
		this.collection = new ArrayList<TargetStream>(collection);
	}
	
	public CollectionTargetStream(String charset, TargetStream... resources) {
		this(charset, Arrays.asList(resources));
	}

	private static final Mapper<TargetStream,OutputStream> targetStreamToOutputStream = new Mapper<TargetStream,OutputStream>() {
		@Override
		public OutputStream map(TargetStream x) {
			try {
				return x.getOutputStream();
			}
			catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
	};

	@Override
	public String getName() {
		return Strings.joinStrings(collection, ", ");
	}
	
	private static final class TeeOutputStream extends OutputStream {
		private final Collection<OutputStream> collection;
		
		private TeeOutputStream(Collection<OutputStream> collection) {
			super();
			this.collection = collection;
		}

		@Override
		public void write(int c) throws IOException {
			for (OutputStream os : collection)
				os.write(c);
		}

		@Override
		public void close() throws IOException {
			for (OutputStream os : collection)
				os.close();
		}

		@Override
		public void flush() throws IOException {
			for (OutputStream os : collection)
				os.flush();
		}

		@Override
		public void write(byte[] buf, int start, int off) throws IOException {
			for (OutputStream os : collection)
				os.write(buf, start, off);
		}

		@Override
		public void write(byte[] buf) throws IOException {
			for (OutputStream os : collection)
				os.write(buf);
		}
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		Collection<OutputStream> outputStreams = new ArrayList<OutputStream>(collection.size());
		Mappers.apply(targetStreamToOutputStream, collection, outputStreams);
		return new TeeOutputStream(outputStreams);
	}

	@Override
	public boolean check(Logger logger) {
		boolean result = true;
		for (TargetStream ts : collection)
			result = ts.check(logger) && result;
		return result;
	}

	@Override
	public String toString() {
		return "collection: " + Strings.joinStrings(collection, ", ");
	}
}
