package org.bibliome.util.marshall;

import java.nio.ByteBuffer;

/**
 * Data decoder.
 * @author rbossy
 *
 * @param <T>
 */
public interface Decoder<T> {
	/**
	 * Decodes the data in the specified buffer.
	 * It is the caller responsibility to set the buffer position at the beginning of a marshalled object.
	 * @param buffer
	 * @return the decoded object.
	 */
	T decode1(ByteBuffer buffer);
	
	/**
	 * Second pass data decoding.
	 * The object will be in the caller unmarshaller cache.
	 * Implement this method for data structures with circular references.
	 * @param buffer
	 * @param object
	 */
	void decode2(ByteBuffer buffer, T object);
}
