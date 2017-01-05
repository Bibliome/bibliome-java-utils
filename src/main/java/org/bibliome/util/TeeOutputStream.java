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

package org.bibliome.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A tee output stream dispatches the bytes written into it to several client streams.
 * @author rbossy
 *
 */
public class TeeOutputStream extends OutputStream {
	private final OutputStream[] outputStreams;

	/**
	 * Creates a new tee output stream with the specified clients.
	 * @param outputStreams
	 */
	public TeeOutputStream(OutputStream... outputStreams) {
		super();
		this.outputStreams = new OutputStream[outputStreams.length];
		System.arraycopy(outputStreams, 0, this.outputStreams, 0, outputStreams.length);
	}

	@Override
	public void write(int b) throws IOException {
		for (OutputStream out : outputStreams)
			out.write(b);
	}

	@Override
	public void close() throws IOException {
		super.close();
		for (OutputStream out : outputStreams)
			out.close();
	}

	@Override
	public void flush() throws IOException {
		super.flush();
		for (OutputStream out : outputStreams)
			out.flush();		
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (OutputStream out : outputStreams)
			out.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		for (OutputStream out : outputStreams)
			out.write(b);
	}
}
