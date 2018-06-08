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
	
	static int REFERENCE_SIZE = 8;
	
	/**
	 * Encodes the specified object in the specified buffer.
	 * It is the caller responsibility to set the buffer position and limit.
	 * @param object
	 * @param buf
	 * @throws IOException
	 */
	void encode(T object, ByteBuffer buf) throws IOException;
}
