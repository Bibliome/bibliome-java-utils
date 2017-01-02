package org.bibliome.util.trie;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.bibliome.util.marshall.Encoder;
import org.bibliome.util.marshall.Marshaller;

class StateEncoder<T> implements Encoder<State<T>> {
	private static int STATE_SIZE = 12;
	
	private Marshaller<State<T>> marshaller;
	private Marshaller<List<T>> valuesMarshaller;
	
	@Override
	public int getSize(State<T> object) {
		int result = 4;
		for (State<T> t = object; t != null; t = t.getNextSibling())
			result += STATE_SIZE;
		return result;
	}

	@Override
	public void encode(State<T> object, ByteBuffer buf) throws IOException {
		if (marshaller == null || valuesMarshaller == null)
			throw new IllegalStateException();
		int n = (buf.remaining() - 4) / STATE_SIZE;
		buf.putInt(n);
		for (State<T> t = object; t != null; t = t.getNextSibling()) {
			buf.putInt(t.getChar());
			buf.putInt(marshaller.write(t.getFirstTransition()));
			buf.putInt(valuesMarshaller.write(t.getValues()));
		}
	}

	void setMarshaller(Marshaller<State<T>> marshaller) {
		this.marshaller = marshaller;
	}

	void setValuesMarshaller(Marshaller<List<T>> valuesMarshaller) {
		this.valuesMarshaller = valuesMarshaller;
	}
}
