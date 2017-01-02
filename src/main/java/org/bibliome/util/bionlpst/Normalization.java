package org.bibliome.util.bionlpst;

public class Normalization extends BioNLPSTAnnotation {
	private final String annotationId;
	private final String referent;
	private BioNLPSTAnnotation annotation;
	
	public Normalization(String source, int lineno, BioNLPSTDocument document, Visibility visibility, String id, String type, String annotationId, String referent) throws BioNLPSTException {
		super(source, lineno, document, visibility, id, type);
		this.annotationId = annotationId;
		this.referent = referent;
	}

	public String getAnnotationId() {
		return annotationId;
	}

	public String getReferent() {
		return referent;
	}

	public BioNLPSTAnnotation getAnnotation() {
		return annotation;
	}

	@Override
	public void resolveIds() throws BioNLPSTException {
		annotation = resolveId(annotationId);
		annotation.addNormalization(this);
	}

	@Override
	public <R,P> R accept(BioNLPSTAnnotationVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	@Override
	public AnnotationKind getKind() {
		return AnnotationKind.NORMALIZATION;
	}
}
