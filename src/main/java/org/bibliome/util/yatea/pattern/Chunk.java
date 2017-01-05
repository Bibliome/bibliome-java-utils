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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Chunk extends Subterm {
	private final List<Subterm> subterms = new ArrayList<Subterm>(5);
	private Subterm head;
	private final List<Subterm> modifiers = new ArrayList<Subterm>(2);
	
	public void addSubterm(Subterm subterm) {
		switch (subterm.getRole()) {
			case HEAD:
				if (head != null) {
					throw new RuntimeException();
				}
				head = subterm;
				break;
			case MODIFIER:
				modifiers.add(subterm);
				break;
			case NONE:
				break;
		}
		subterms.add(subterm);
	}

	public List<Subterm> getSubterms() {
		return Collections.unmodifiableList(subterms);
	}

	public Subterm getHead() {
		return head;
	}

	public List<Subterm> getModifiers() {
		return Collections.unmodifiableList(modifiers);
	}

	@Override
	public <R,P> R accept(SubtermVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}
}