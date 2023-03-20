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
	 * @throws IOException 
	 */
	T decode1(DataBuffer buffer);
	
	/**
	 * Second pass data decoding.
	 * The object will be in the caller unmarshaller cache.
	 * Implement this method for data structures with circular references.
	 * @param buffer
	 * @param object
	 * @throws IOException 
	 */
	void decode2(DataBuffer buffer, T object);
}
