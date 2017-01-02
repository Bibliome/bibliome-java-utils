package org.bibliome.util.format;

import java.io.IOException;
import java.util.Map;

public class Constant implements FormatElement {
	private final CharSequence value;

	public Constant(CharSequence value) {
		super();
		this.value = value;
	}

	@Override
	public void build(Appendable out, Map<String,? extends CharSequence> vars, CharSequence defaultValue) throws IOException {
		out.append(value);
	}
}
