package org.bibliome.util.marshall;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Data marshaller.
 * @author rbossy
 *
 * @param <T> type of the marshalled objects
 */
public class Marshaller<T> extends AbstractMarshaller<T> {
	private final Encoder<T> encoder;
	private final WriteCache<T> cache;
	
	/**
	 * Creates a new data marshaller.
	 * @param channel channel where data is marshalled, must be opened in write mode
	 * @param encoder
	 * @param cache
	 */
	public Marshaller(FileChannel channel, Encoder<T> encoder, WriteCache<T> cache) {
		super(channel);
		this.encoder = encoder;
		this.cache = cache;
	}
	
	/**
	 * Creates a new data marshaller without write cache.
	 * @param channel channel where data is marshalled, must be opened in write mode
	 * @param encoder
	 */
	public Marshaller(FileChannel channel, Encoder<T> encoder) {
		this(channel, encoder, null);
	}

	/**
	 * Marshalls the specified object.
	 * If this marshaller has a write cache, then the specified object will be looked in the cache.
	 * The object will be marshalled and written in the channel given to the constructor iff it is not in the write cache.
	 * @param object
	 * @return the position of the specified object serialization, -1 if object is null
	 * @throws IOException
	 */
	public int write(T object) throws IOException {
		if (object == null)
			return -1;
		
		// test cache presence
		if (cache != null && cache.contains(object))
			return cache.get(object);
		
		// allocate cache and file space for this object
		int result = getPosition();
		if (cache != null)
			cache.put(object, result);
		int size = encoder.getSize(object);
		channel.position(result + size);

		// encode object
		ByteBuffer buf = ByteBuffer.allocate(size);
		encoder.encode(object, buf);

		// write data
		buf.flip();
		channel.write(buf, result);

		return result;
	}

	public WriteCache<T> getCache() {
		return cache;
	}
}
