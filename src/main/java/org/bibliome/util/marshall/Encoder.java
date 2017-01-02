package org.bibliome.util.marshall;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Data encoder.
 * @author rbossy
 *
 * @param <T>
 */
public interface Encoder<T> {
	/**
	 * Returns the size in bytes necessary to encode the specified object.
	 * @param object
	 */
	int getSize(T object);
	
	/**
	 * Encodes the specified object in the specified buffer.
	 * It is the caller responsibility to set the buffer position and limit.
	 * @param object
	 * @param buf
	 * @throws IOException
	 */
	void encode(T object, ByteBuffer buf) throws IOException;
}
