package org.bibliome.util.trie;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bibliome.util.marshall.Decoder;
import org.bibliome.util.marshall.Unmarshaller;

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
			result.add(unmarshaller.read(buffer.getInt()));
		return result;
	}

	@Override
	public void decode2(ByteBuffer buffer, List<T> object) {
	}
}