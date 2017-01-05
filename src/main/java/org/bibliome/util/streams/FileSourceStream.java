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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import org.bibliome.util.files.InputFile;

public class FileSourceStream extends AbstractSingleSourceStream {
	private final InputFile file;

	public FileSourceStream(String charset, CompressionFilter compressionFilter, InputFile file) {
		super(charset, compressionFilter);
		this.file = file;
	}

	public FileSourceStream(String charset, CompressionFilter compressionFilter, String file) {
		super(charset, compressionFilter);
		this.file = new InputFile(file);
	}

	public FileSourceStream(String charset, InputFile file) {
		super(charset);
		this.file = file;
	}

	public FileSourceStream(String charset, String file) {
		super(charset);
		this.file = new InputFile(file);
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		InputStream result = compressionFilter.getInputStream(new FileInputStream(file));
		setStreamName(result, file.getAbsolutePath());
		return result;
	}

	@Override
	public boolean check(Logger logger) {
		return file.check(logger);
	}

	@Override
	public String toString() {
		return file.toString();
	}

	@Override
	public Collection<String> getStreamNames() {
		return Collections.singleton(file.getAbsolutePath());
	}
}
