package org.bibliome.util.bionlpst.schema;

import java.util.Collection;
import java.util.HashSet;

import org.bibliome.util.bionlpst.BioNLPSTAnnotation;
import org.bibliome.util.bionlpst.BioNLPSTException;
import org.bibliome.util.bionlpst.BioNLPSTRelation;
import org.bibliome.util.bionlpst.Event;

public class EventSchema extends WithArgsSchema {
	private final Collection<String> triggerTypes = new HashSet<String>();

	public EventSchema(DocumentSchema documentSchema, String type) throws BioNLPSTException {
		super(documentSchema, type);
	}

	public void addTriggerType(String type) {
		triggerTypes.add(type);
	}
	
	@Override
	public Void visit(BioNLPSTRelation relation, Collection<String> param) {
		param.add(unexpectedKind(relation));
		return null;
	}

	@Override
	public Void visit(Event event, Collection<String> param) {
		checkArgs(param, event);
		BioNLPSTAnnotation trigger = event.getTrigger();
		DocumentSchema.checkType(param, event, "trigger", triggerTypes, trigger);
		return null;
	}

	@Override
	protected String getExpectedKind() {
		return "event";
	}

	@Override
	public <R,P> R accept(AnnotationSchemaVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}
}
