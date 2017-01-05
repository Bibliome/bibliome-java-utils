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

package org.bibliome.util.trie;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.bibliome.util.marshall.Encoder;
import org.bibliome.util.marshall.Marshaller;

class ValuesEncoder<T> implements Encoder<List<T>> {
	private final Marshaller<T> marshaller;
	
	ValuesEncoder(Marshaller<T> marshaller) {
		super();
		this.marshaller = marshaller;
	}

	@Override
	public int getSize(List<T> object) {
		return (1 + object.size()) * 4;
	}

	@Override
	public void encode(List<T> object, ByteBuffer buf) throws IOException {
		buf.putInt(object.size());
		for (T elt : object)
			buf.putInt(marshaller.write(elt));
	}
}