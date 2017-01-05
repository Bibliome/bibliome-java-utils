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

package org.bibliome.util.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatSequence implements FormatElement {
	public static final Pattern DEFAULT_VARIABLE_PATTERN = Pattern.compile("\\{(.*?)\\}");
	
	private final List<FormatElement> elements = new ArrayList<FormatElement>();

	public FormatSequence() {
		super();
	}
	
	public FormatSequence(CharSequence in, Pattern varPat) {
		Matcher m = varPat.matcher(in);
		int last = 0;
		while (m.find()) {
			int pos = m.start();
			if (pos > last) {
				CharSequence s = in.subSequence(last, pos);
				addConstant(s);
			}
			String name = m.group(1);
			addVariable(name);
			last = m.end();
		}
		if (last < in.length()) {
			addConstant(in.subSequence(last, in.length()));
		}
	}
	
	public FormatSequence(CharSequence in, String varPat) {
		this(in, Pattern.compile(varPat));
	}
	
	public FormatSequence(CharSequence in) {
		this(in, DEFAULT_VARIABLE_PATTERN);
	}
	
	public void addElement(FormatElement elt) {
		elements.add(elt);
	}
	
	public void addConstant(CharSequence value) {
		addElement(new Constant(value));
	}
	
	public void addVariable(String name) {
		addElement(new Variable(name));
	}
	
	public void addAll(Collection<? extends FormatElement> c, CharSequence sep) {
		if (sep == null || sep.length() == 0) {
			elements.addAll(c);
			return;
		}
		Constant sepElt = new Constant(sep);
		boolean notFirst = !elements.isEmpty();
		for (FormatElement elt : c) {
			if (notFirst) {
				addElement(sepElt);
			}
			else {
				notFirst = true;
			}
			elements.add(elt);
		}
	}
	
	public void addAll(FormatSequence fs, CharSequence sep) {
		addAll(fs.elements, sep);
	}

	public void addAll(Collection<? extends FormatElement> c) {
		addAll(c, null);
	}
	
	public void addAll(FormatSequence fs) {
		addAll(fs.elements, null);
	}
	
	@Override
	public void build(Appendable out, Map<String,? extends CharSequence> vars, CharSequence defaultValue) throws IOException {
		for (FormatElement elt : elements) {
			elt.build(out, vars, defaultValue);
		}
	}
	
	public String build(Map<String,? extends CharSequence> vars, CharSequence defaultValue) {
		StringBuilder out = new StringBuilder();
		try {
			build(out, vars, defaultValue);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out.toString();
	}
}
