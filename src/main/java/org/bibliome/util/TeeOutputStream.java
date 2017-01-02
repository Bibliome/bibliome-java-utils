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
