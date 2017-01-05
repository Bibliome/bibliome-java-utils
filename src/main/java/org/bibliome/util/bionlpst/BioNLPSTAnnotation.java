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

package org.bibliome.util.bionlpst;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

public abstract class BioNLPSTAnnotation extends Sourced {
	private final String id;
	private final String type;
	private final Collection<Modification> modifications = new LinkedHashSet<Modification>();
	private final Collection<Normalization> normalizations = new LinkedHashSet<Normalization>();

	protected BioNLPSTAnnotation(String source, int lineno, BioNLPSTDocument document, Visibility visibility, String id, String type) throws BioNLPSTException {
		super(source, lineno, document, visibility);
		this.id = id;
		this.type = type;
		document.addAnnotation(this);
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}
	
	public Collection<Modification> getModifications() {
		return Collections.unmodifiableCollection(modifications);
	}

	public Collection<Normalization> getNormalizations() {
		return Collections.unmodifiableCollection(normalizations);
	}
	
	void addModification(Modification mod) {
		modifications.add(mod);
	}
	
	void addNormalization(Normalization norm) {
		normalizations.add(norm);
	}

	public abstract void resolveIds() throws BioNLPSTException;
	
	public abstract AnnotationKind getKind();
	
	public abstract <R,P> R accept(BioNLPSTAnnotationVisitor<R,P> visitor, P param);
}
