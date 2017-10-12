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
import java.nio.ByteBuffer;

/**
 * Encodere/decoder for String objects.
 * @author rbossy
 *
 */
public enum StringCodec implements Encoder<String>, Decoder<String> {
	INSTANCE;

	@Override
	public int getSize(String object) {
		return 4 + object.length() * 2;
	}

	@Override
	public void encode(String object, ByteBuffer buf) throws IOException {
		buf.putInt(object.length());
		for (int i = 0; i < object.length(); ++i)
			buf.putChar(object.charAt(i));
	}

	@Override
	public String decode1(ByteBuffer buffer) {
		int len = buffer.getInt();
		char[] s = new char[len];
		for (int i = 0; i < len; ++i)
			s[i] = buffer.getChar();
		return new String(s);
	}

	@Override
	public void decode2(ByteBuffer buffer, String object) {
	}
}
