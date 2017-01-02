package org.bibliome.util.bionlpst;

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
