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

import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.InputFile;

public class FileSourceStream extends AbstractMultipleSourceStream {
	private final FileFilter fileFilter;
	private final InputFile file;
	private final CompressionFilter compressionFilter;
	
	public FileSourceStream(String charset, CompressionFilter compressionFilter, InputFile file, FileFilter fileFilter) {
		super(charset);
		this.file = file;
		this.fileFilter = fileFilter;
		this.compressionFilter = compressionFilter;
	}

	public FileSourceStream(String charset, CompressionFilter compressionFilter, InputFile file) {
		this(charset, compressionFilter, file, AcceptAllFiles.INSTANCE);
	}

	public FileSourceStream(String charset, CompressionFilter compressionFilter, String file) {
		this(charset, compressionFilter, new InputFile(file));
	}

	public FileSourceStream(String charset, InputFile file) {
		super(charset);
		this.file = file;
		this.fileFilter = AcceptAllFiles.INSTANCE;
		this.compressionFilter = CompressionFilter.NONE;
	}

	public FileSourceStream(String charset, String file) {
		this(charset, new InputFile(file));
	}

	@Override
	public Collection<String> getStreamNames() {
		return Collections.singleton(file.getAbsolutePath());
	}

	@Override
	public Iterator<InputStream> getInputStreams() throws IOException {
		if (fileFilter.accept(file)) {
			return Iterators.singletonIterator(getFileInputStream());
		}
		return Collections.emptyIterator();
	}

	private InputStream getFileInputStream() throws FileNotFoundException, IOException {
		String streamName = file.getAbsolutePath();
		InputStream result = compressionFilter.getInputStream(new FileInputStream(file), streamName);
		setStreamName(result, streamName);
		return result;
	}

	@Override
	public boolean check(Logger logger) {
		return file.check(logger);
	}

	@Override
	protected String getCollectiveName() {
		return file.getAbsolutePath();
	}
}
