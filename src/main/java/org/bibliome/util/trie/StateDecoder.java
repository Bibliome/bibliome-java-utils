package org.bibliome.util.trie;

import java.nio.ByteBuffer;
import java.util.List;

import org.bibliome.util.marshall.Decoder;
import org.bibliome.util.marshall.MReference;
import org.bibliome.util.marshall.MReferenceImpl;

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