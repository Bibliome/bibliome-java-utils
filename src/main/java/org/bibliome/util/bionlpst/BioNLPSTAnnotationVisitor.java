package org.bibliome.util.bionlpst;

public interface BioNLPSTAnnotationVisitor<R,P> {
	R visit(TextBound textBound, P param);
	R visit(BioNLPSTRelation relation, P param);
	R visit(Event event, P param);
	R visit(Normalization normalization, P param);
	R visit(Modification modification, P param);
}
