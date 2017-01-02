package org.bibliome.util.alvisae;


public class AnnotationReference {
	private final AnnotationSet annotationSet;
	private final String id;
	
	public AnnotationReference(AnnotationSet annotationSet, String id) {
		super();
		this.annotationSet = annotationSet;
		this.id = id;
	}

	public AnnotationSet getAnnotationSet() {
		return annotationSet;
	}

	public String getId() {
		return id;
	}
	
	public AlvisAEDocument getDocument() {
		return annotationSet.getDocument();
	}
}
