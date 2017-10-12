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
import java.util.List;

import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.MReference;
import fr.inra.maiage.bibliome.util.marshall.MReferenceImpl;

class StateDecoder<T> implements Decoder<State<T>> {
	private final Trie<T> trie;
	
	StateDecoder(Trie<T> trie) {
		super();
		this.trie = trie;
	}

	@Override
	public State<T> decode1(ByteBuffer buffer) {
		State<T> result = null;
		int n = buffer.getInt();
		for (int i = 0; i < n; ++i) {
			int c = buffer.getInt();
			int ft = buffer.getInt();
			int v = buffer.getInt();
			MReference<State<T>> firstTransition = new MReferenceImpl<State<T>>(ft, trie.getStateUnmarshaller());
			MReference<List<T>> values = new MReferenceImpl<List<T>>(v, trie.getValuesUnmarshaller());
			result = new State<T>(c, result, firstTransition, values);
		}
		return result;
	}

	@Override
	public void decode2(ByteBuffer buffer, State<T> object) {
	}
}