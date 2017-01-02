package org.bibliome.util.bionlpst.schema;

import java.util.Collection;

import org.bibliome.util.bionlpst.BioNLPSTException;
import org.bibliome.util.bionlpst.BioNLPSTRelation;
import org.bibliome.util.bionlpst.Event;

public class RelationSchema extends WithArgsSchema {
	public RelationSchema(DocumentSchema documentSchema, String type) throws BioNLPSTException {
		super(documentSchema, type);
	}

	@Override
	public Void visit(BioNLPSTRelation relation, Collection<String> param) {
		checkArgs(param, relation);
		return null;
	}

	@Override
	public Void visit(Event event, Collection<String> param) {
		param.add(unexpectedKind(event));
		return null;
	}

	@Override
	protected String getExpectedKind() {
		return "relation";
	}

	@Override
	public <R,P> R accept(AnnotationSchemaVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}
}
