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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import fr.inra.maiage.bibliome.util.marshall.Encoder;
import fr.inra.maiage.bibliome.util.marshall.Marshaller;

class StateEncoder<T> implements Encoder<State<T>> {
	private static int STATE_SIZE = 20;
	
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
			buf.putLong(marshaller.write(t.getFirstTransition()));
			buf.putLong(valuesMarshaller.write(t.getValues()));
		}
	}

	void setMarshaller(Marshaller<State<T>> marshaller) {
		this.marshaller = marshaller;
	}

	void setValuesMarshaller(Marshaller<List<T>> valuesMarshaller) {
		this.valuesMarshaller = valuesMarshaller;
	}
}
