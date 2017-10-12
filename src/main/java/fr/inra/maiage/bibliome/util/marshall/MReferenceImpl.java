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

/**
 * Marshall reference implementation.
 * @author rbossy
 *
 * @param <T>
 */
public class MReferenceImpl<T> implements MReference<T> {
	private final Unmarshaller<T> unmarshaller;
	private final int position;
	private T value;
	
	/**
	 * Creates a new marshall reference at the specified position using the specified unmarshaller for dereferencing.
	 * Dereferencing is lazy.
	 * @param position
	 * @param unmarshaller
	 */
	public MReferenceImpl(int position, Unmarshaller<T> unmarshaller) {
		super();
		this.position = position;
		this.unmarshaller = unmarshaller;
	}

	@Override
	public T get() {
		if (value == null)
			value = unmarshaller.read(position);
		return value;
	}

	@Override
	public int getPosition() {
		return position;
	}
}
