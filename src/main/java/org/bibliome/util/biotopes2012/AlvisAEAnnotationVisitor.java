package org.bibliome.util.biotopes2012;

public interface AlvisAEAnnotationVisitor<R,P> {
	R visit(TextBound textBound, P param);
	R visit(Group group, P param);
	R visit(Relation relation, P param);
}
