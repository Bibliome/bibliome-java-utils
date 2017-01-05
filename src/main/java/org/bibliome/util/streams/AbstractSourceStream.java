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
import java.util.Map;
import java.util.WeakHashMap;

public abstract class AbstractSourceStream implements SourceStream {
	private static final Map<Object,String> streamNames = new WeakHashMap<Object,String>();

	protected final String charset;
	
	protected AbstractSourceStream(String charset) {
		super();
		this.charset = charset;
	}

	@Override
	public String getStreamName(Object stream) {
		String result = streamNames.get(stream);
		if (result == null)
			throw new IllegalArgumentException();
		return result;
	}
	
	protected static void setStreamName(Object stream, String name) {
		streamNames.put(stream, name);
	}

	@Override
	public Reader getReader() throws IOException {
		InputStream is = getInputStream();
		Reader result = new InputStreamReader(is, charset);
		setStreamName(result, getStreamName(is));
		return result;
	}

	@Override
	public BufferedReader getBufferedReader() throws IOException {
		Reader r = getReader();
		BufferedReader result = new BufferedReader(r);
		setStreamName(result, getStreamName(r));
		return result;
	}

	@Override
	public String getCharset() {
		return charset;
	}
}
