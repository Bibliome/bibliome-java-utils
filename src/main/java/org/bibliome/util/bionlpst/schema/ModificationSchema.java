package org.bibliome.util.bionlpst.schema;

import java.util.Collection;
import java.util.HashSet;

import org.bibliome.util.bionlpst.BioNLPSTAnnotation;
import org.bibliome.util.bionlpst.BioNLPSTException;
import org.bibliome.util.bionlpst.BioNLPSTRelation;
import org.bibliome.util.bionlpst.Event;
import org.bibliome.util.bionlpst.Modification;
import org.bibliome.util.bionlpst.Normalization;
import org.bibliome.util.bionlpst.TextBound;

public class ModificationSchema extends AnnotationSchema {
	private final Collection<String> types = new HashSet<String>();
	
	public ModificationSchema(DocumentSchema documentSchema, String type) throws BioNLPSTException {
		super(documentSchema, type);
	}

	public void addAnnotationType(String type) {
		types.add(type);
	}

	@Override
	public Void visit(TextBound textBound, Collection<String> param) {
		param.add(unexpectedKind(textBound));
		return null;
	}

	@Override
	public Void visit(BioNLPSTRelation relation, Collection<String> param) {
		param.add(unexpectedKind(relation));
		return null;
	}

	@Override
	public Void visit(Event event, Collection<String> param) {
		param.add(unexpectedKind(event));
		return null;
	}

	@Override
	public Void visit(Normalization normalization, Collection<String> param) {
		param.add(unexpectedKind(normalization));
		return null;
	}

	@Override
	public Void visit(Modification modification, Collection<String> param) {
		BioNLPSTAnnotation annotation = modification.getAnnotation();
		DocumentSchema.checkType(param, modification, "annotation", types, annotation);
		return null;
	}

	@Override
	protected String getExpectedKind() {
		return "modification";
	}

	@Override
	Collection<String> getReferencedTypes() {
		return new HashSet<String>(types);
	}

	@Override
	public <R,P> R accept(AnnotationSchemaVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}
}
