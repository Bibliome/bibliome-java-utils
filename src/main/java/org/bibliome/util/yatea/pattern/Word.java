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

package org.bibliome.util.yatea.pattern;

public final class Word extends Subterm {
	private final String value;
	private final WordAttribute attribute;
	
	public Word(String value, WordAttribute attribute) {
		super();
		this.value = value;
		this.attribute = attribute;
	}

	public Word(String value) {
		this(value, WordAttribute.get(value));
	}

	public String getValue() {
		return value;
	}

	public WordAttribute getAttribute() {
		return attribute;
	}

	@Override
	public <R,P> R accept(SubtermVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}
}
