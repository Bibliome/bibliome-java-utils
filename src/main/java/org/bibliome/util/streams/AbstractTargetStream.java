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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;

public abstract class AbstractTargetStream implements TargetStream {
	protected final String charset;
	
	protected AbstractTargetStream(String charset) {
		super();
		this.charset = charset;
	}

	@Override
	public Writer getWriter() throws IOException {
		return new OutputStreamWriter(getOutputStream(), charset);
	}

	@Override
	public PrintStream getPrintStream() throws IOException {
		return new PrintStream(getOutputStream(), true, charset);
	}

	@Override
	public BufferedWriter getBufferedWriter() throws IOException {
		return new BufferedWriter(getWriter());
	}
}
