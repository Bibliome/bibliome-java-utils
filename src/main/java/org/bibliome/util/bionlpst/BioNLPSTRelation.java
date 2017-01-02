package org.bibliome.util.bionlpst;

public class BioNLPSTRelation extends AnnotationWithArgs {
	public BioNLPSTRelation(String source, int lineno, BioNLPSTDocument document, Visibility visibility, String id, String type) throws BioNLPSTException {
		super(source, lineno, document, visibility, id, type);
	}

	@Override
	public <R,P> R accept(BioNLPSTAnnotationVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	@Override
	public AnnotationKind getKind() {
		return AnnotationKind.RELATION;
	}
}
