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

package fr.inra.maiage.bibliome.util.bionlpst;

import java.util.Collection;
import java.util.EnumSet;

import fr.inra.maiage.bibliome.util.filters.Filter;

public class KindFilter implements Filter<BioNLPSTAnnotation> {
	private final Collection<AnnotationKind> kinds;

	private KindFilter(AnnotationKind first, AnnotationKind... rest) {
		super();
		this.kinds = EnumSet.of(first, rest);
	}
	
	public void addKind(AnnotationKind kind) {
		kinds.add(kind);
	}

	@Override
	public boolean accept(BioNLPSTAnnotation x) {
		return kinds.contains(x.getKind());
	}
}
