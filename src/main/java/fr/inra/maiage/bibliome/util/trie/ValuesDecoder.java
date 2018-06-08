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

package fr.inra.maiage.bibliome.util.trie;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.Unmarshaller;

class ValuesDecoder<T> implements Decoder<List<T>> {
	private final Unmarshaller<T> unmarshaller;

	ValuesDecoder(Unmarshaller<T> unmarshaller) {
		super();
		this.unmarshaller = unmarshaller;
	}

	@Override
	public List<T> decode1(ByteBuffer buffer) {
		int len = buffer.getInt();
		List<T> result = new ArrayList<T>(len);
		for (int i = 0; i < len; ++i)
			result.add(unmarshaller.read(buffer.getLong()));
		return result;
	}

	@Override
	public void decode2(ByteBuffer buffer, List<T> object) {
	}
}