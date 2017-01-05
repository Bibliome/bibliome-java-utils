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

package org.bibliome.util.filelines;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

public final class LineIterator implements Iterator<String> {
	private final BufferedReader reader;
	private String next;

	public LineIterator(BufferedReader reader) throws IOException {
		super();
		this.reader = reader;
		this.next = reader.readLine();
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public String next() {
		String result = next;
		try {
			next = reader.readLine();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
