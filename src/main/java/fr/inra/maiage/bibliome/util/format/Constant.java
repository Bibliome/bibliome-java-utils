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

package fr.inra.maiage.bibliome.util.format;

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
