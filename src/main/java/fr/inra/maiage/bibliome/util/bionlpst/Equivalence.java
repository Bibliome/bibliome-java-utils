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
import java.util.Collections;
import java.util.LinkedHashSet;

public class Equivalence extends Sourced {
	private final Collection<String> annotationIds = new LinkedHashSet<String>();
	private final Collection<BioNLPSTAnnotation> annotations = new LinkedHashSet<BioNLPSTAnnotation>();

	public Equivalence(String source, int lineno, BioNLPSTDocument document, Visibility visibility) {
		super(source, lineno, document, visibility);
		document.addEquivalence(this);
	}
	
	public void resolveIds() throws BioNLPSTException {
		annotations.clear();
		for (String id : annotationIds) {
			annotations.add(resolveId(id));
		}
	}

	public Collection<String> getAnnotationIds() {
		return Collections.unmodifiableCollection(annotationIds);
	}

	public Collection<BioNLPSTAnnotation> getAnnotations() {
		return Collections.unmodifiableCollection(annotations);
	}
	
	void addAnnotationId(String id) {
		annotationIds.add(id);
	}
}
