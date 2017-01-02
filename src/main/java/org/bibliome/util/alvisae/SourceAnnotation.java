package org.bibliome.util.alvisae;

public class SourceAnnotation {
	private final AlvisAEAnnotation annotation;
	private final int status;
	
	public SourceAnnotation(AlvisAEAnnotation annotation, int status) {
		super();
		this.annotation = annotation;
		this.status = status;
	}

	public AlvisAEAnnotation getAnnotation() {
		return annotation;
	}

	public int getStatus() {
		return status;
	}
}
