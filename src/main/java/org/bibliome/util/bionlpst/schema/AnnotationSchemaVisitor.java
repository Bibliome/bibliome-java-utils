package org.bibliome.util.bionlpst.schema;

public interface AnnotationSchemaVisitor<R,P> {
	R visit(EventSchema event, P param);
	R visit(ModificationSchema mod, P param);
	R visit(NormalizationSchema norm, P param);
	R visit(RelationSchema rel, P param);
	R visit(TextBoundSchema txt, P param);
}
