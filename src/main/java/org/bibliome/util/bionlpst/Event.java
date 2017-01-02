package org.bibliome.util.bionlpst;

public class Event extends AnnotationWithArgs {
	private final String triggerId;
	private BioNLPSTAnnotation trigger;
	
	public Event(String source, int lineno, BioNLPSTDocument document, Visibility visibility, String id, String type, String triggerId) throws BioNLPSTException {
		super(source, lineno, document, visibility, id, type);
		this.triggerId = triggerId;
	}

	public String getTriggerId() {
		return triggerId;
	}

	public BioNLPSTAnnotation getTrigger() {
		return trigger;
	}

	@Override
	public void resolveIds() throws BioNLPSTException {
		super.resolveIds();
		trigger = resolveId(triggerId);
	}

	@Override
	public <R,P> R accept(BioNLPSTAnnotationVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	@Override
	public AnnotationKind getKind() {
		return AnnotationKind.EVENT;
	}
}
