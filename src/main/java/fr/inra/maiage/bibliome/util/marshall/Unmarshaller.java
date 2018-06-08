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

package fr.inra.maiage.bibliome.util.marshall;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Data unmarshaller.
 * @author rbossy
 *
 * @param <T> type of the unmarshalled objects
 */
public class Unmarshaller<T> extends AbstractMarshaller<T> implements Closeable {
	private final Decoder<T> decoder;
	private final ReadCache<T> cache;
	//private final MappedByteBuffer buf;
	private long startPosition;
	
	/**
	 * Creates a new unmarshaller.
	 * @param channel channel from which objects are unmarshalled, must be opened in read mode
	 * @param decoder
	 * @param cache
	 * @throws IOException
	 */
	public Unmarshaller(FileChannel channel, Decoder<T> decoder, ReadCache<T> cache) throws IOException {
		super(channel);
		this.decoder = decoder;
		this.cache = cache;
		this.startPosition = getPosition(channel);
	}
	
	/**
	 * Creates a new unmarshaller without read cache.
	 * @param channel channel from which objects are unmarshalled, must be opened in read mode
	 * @param decoder
	 * @throws IOException
	 */
	public Unmarshaller(FileChannel channel, Decoder<T> decoder) throws IOException {
		this(channel, decoder, null);
	}
	
	/**
	 * Creates a new unmarshaller.
	 * @param path
	 * @param decoder
	 * @param cache
	 * @throws IOException
	 */
	public Unmarshaller(Path path, Decoder<T> decoder, ReadCache<T> cache) throws IOException {
		this(FileChannel.open(path, StandardOpenOption.READ), decoder, cache);
	}
	
	/**
	 * Creates a new unmarshaller without read cache.
	 * @param path
	 * @param decoder
	 * @throws IOException
	 */
	public Unmarshaller(Path path, Decoder<T> decoder) throws IOException {
		this(path, decoder, null);
	}
	
	/**
	 * Creates a new unmarshaller.
	 * @param file
	 * @param decoder
	 * @param cache
	 * @throws IOException
	 */
	public Unmarshaller(File file, Decoder<T> decoder, ReadCache<T> cache) throws IOException {
		this(file.toPath(), decoder, cache);
	}
	
	/**
	 * Creates a new unmarshaller without read cache.
	 * @param file
	 * @param decoder
	 * @throws IOException
	 */
	public Unmarshaller(File file, Decoder<T> decoder) throws IOException {
		this(file, decoder, null);
	}

	/**
	 * Unmarshalls an object at the specified channel position.
	 * Returns null if position == -1.
	 * If this unmarshaller has a read cache and the specified position is in the cache, then the cached object is returned.
	 * If this unmarshaller has a read cache and the specified position is not in the cache, then the returned object is put in the cache.
	 * @param position
	 * @return the unmarshalled object.
	 * @throws IOException 
	 */
	public T read(long position) {
		if (position == -1)
			return null;

		T result;
		// test cache presence
		if (cache != null) {
			result = cache.get(position);
			if (result != null)
				return result;
		}
		
		// first pass decode
		ByteBuffer buf = getBuffer(position);
		//buf.position((int) position); // XXX unsafe cast
		result = decoder.decode1(buf);
		
		// second pass decode
		if (cache != null)
			cache.put(position, result);
		decoder.decode2(buf, result);
		
		return result;
	}

	@Override
	public void close() throws IOException {
		if (channel.isOpen())
			channel.close();
	}

	public ReadCache<T> getCache() {
		return cache;
	}
	
	public ByteBuffer getBuffer(long position) {
		try {
			long absPosition = startPosition + position;
			long size = Math.min(channel.size() - absPosition, Integer.MAX_VALUE);
			return channel.map(MapMode.READ_ONLY, absPosition, size);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
//	
//	public ByteBuffer getBuffer() {
//		return buf.duplicate();
//	}
}
