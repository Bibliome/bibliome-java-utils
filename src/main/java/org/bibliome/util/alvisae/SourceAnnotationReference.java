package org.bibliome.util.alvisae;

import java.util.Collection;


public class SourceAnnotationReference {
	private final int annotationSetId;
	private final String annotationId;
	private final int status;
	
	public SourceAnnotationReference(int annotationSetId, String annotationId, int status) {
		super();
		this.annotationSetId = annotationSetId;
		this.annotationId = annotationId;
		this.status = status;
	}

	public String getAnnotationId() {
		return annotationId;
	}

	public int getAnnotationSetId() {
		return annotationSetId;
	}

	public int getStatus() {
		return status;
	}
	
	SourceAnnotationReference update(Collection<AnnotationSet> headAnnotationSets) {
		for (AnnotationSet head : headAnnotationSets) {
			if (annotationSetId == head.getId()) {
				return this;
			}
		}
		for (AnnotationSet head : headAnnotationSets) {
			if (head.hasAnnotation(annotationId)) {
				return new SourceAnnotationReference(head.getId(), annotationId, status);
			}
		}
		return this;
	}
}
