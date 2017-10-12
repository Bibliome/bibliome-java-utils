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

package fr.inra.maiage.bibliome.util.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import fr.inra.maiage.bibliome.util.Iterators;

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
