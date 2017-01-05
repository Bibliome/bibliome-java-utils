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
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

public class InputStreamSourceStream extends AbstractSingleSourceStream {
	private final InputStream is;
	private final String name;
	
	public InputStreamSourceStream(String charset, CompressionFilter compressionFilter, InputStream is, String name) {
		super(charset, compressionFilter);
		this.is = is;
		this.name = name;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		InputStream result = compressionFilter.getInputStream(is);
		setStreamName(result, name);
		return result;
	}

	@Override
	public boolean check(Logger logger) {
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Collection<String> getStreamNames() {
		return Collections.singleton(name);
	}
}
