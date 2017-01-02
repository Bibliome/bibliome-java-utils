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