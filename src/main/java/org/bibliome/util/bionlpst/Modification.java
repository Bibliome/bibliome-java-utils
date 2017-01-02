package org.bibliome.util.bionlpst;

public class Modification extends BioNLPSTAnnotation {
	private final String annotationId;
	private BioNLPSTAnnotation annotation;
	
	public Modification(String source, int lineno, BioNLPSTDocument document, Visibility visibility, String id, String type, String annotationId) throws BioNLPSTException {
		super(source, lineno, document, visibility, id, type);
		this.annotationId = annotationId;
	}

	public String getAnnotationId() {
		return annotationId;
	}

	public BioNLPSTAnnotation getAnnotation() {
		return annotation;
	}

	@Override
	public void resolveIds() throws BioNLPSTException {
		annotation = resolveId(annotationId);
		annotation.addModification(this);
	}

	@Override
	public <R,P> R accept(BioNLPSTAnnotationVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	@Override
	public AnnotationKind getKind() {
		return AnnotationKind.MODIFICATION;
	}
}
