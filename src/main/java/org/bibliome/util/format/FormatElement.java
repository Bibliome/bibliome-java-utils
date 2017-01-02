package org.bibliome.util.format;

import java.io.IOException;
import java.util.Map;

public interface FormatElement {
	void build(Appendable out, Map<String,? extends CharSequence> vars, CharSequence defaultValue) throws IOException;
}
