package fr.inra.maiage.bibliome.util.alvisae;

public interface AnnotationVisitor<R,P> {
	R visit(TextBound tb, P param);
	R visit(Group grp, P param);
	R visit(Relation rel, P param);
}
