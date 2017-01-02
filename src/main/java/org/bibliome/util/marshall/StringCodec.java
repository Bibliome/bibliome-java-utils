package org.bibliome.util.marshall;

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
