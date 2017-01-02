package org.bibliome.util.format;

import java.io.IOException;
import java.util.Map;

public class Variable implements FormatElement {
	private final String name;

	public Variable(String name) {
		super();
		this.name = name;
	}

	@Override
	public void build(Appendable out, Map<String,? extends CharSequence> vars, CharSequence defaultValue) throws IOException {
		out.append(getValue(vars, defaultValue));
	}
	
	private CharSequence getValue(Map<String,? extends CharSequence> vars, CharSequence defaultValue) {
		if (!vars.containsKey(name)) {
			if (defaultValue == null) {
				throw new IllegalArgumentException("unbound variable: " + name);
			}
			return defaultValue;
		}
		return vars.get(name);
	}
}
