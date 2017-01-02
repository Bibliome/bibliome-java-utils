package org.bibliome.util.marshall;

import java.io.IOException;
import java.nio.channels.FileChannel;

public abstract class AbstractMarshaller<T> {
	protected final FileChannel channel;
	
	protected AbstractMarshaller(FileChannel channel) {
		super();
		this.channel = channel;
	}
	
	public static int getPosition(FileChannel channel) throws IOException {
		long result = channel.position();
		if (result > Integer.MAX_VALUE)
			throw new IOException("position exceeds 32-bit value");
		return (int) result;
	}
	
	protected int getPosition() throws IOException {
		return getPosition(channel);
	}

	public FileChannel getChannel() {
		return channel;
	}
}
