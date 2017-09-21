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
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

public class ResourceSourceStream extends AbstractSingleSourceStream {
	private final String name;
	private final URL url;

	public ResourceSourceStream(String charset, CompressionFilter compressionFilter, String name) {
		super(charset, compressionFilter);
		this.name = name;
		Class<?> klass = getClass();
		ClassLoader classLoader = klass.getClassLoader();
		url = classLoader.getResource(name);
		if (url == null) {
			throw new RuntimeException("resource " + name + " not found");
		}
	}

	@Override
	public InputStream getInputStream() throws IOException {
		InputStream result = compressionFilter.getInputStream(url.openStream(), name);
		setStreamName(result, name);
		return result;
	}

	@Override
	public boolean check(Logger logger) {
		if (url != null)
			return true;
		logger.severe("resource " + name + " is not available");
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Collection<String> getStreamNames() {
		return Collections.singleton(url.toString());
	}
}
