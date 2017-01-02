package org.bibliome.util.bionlpst.schema;

import java.util.Collection;

import org.bibliome.util.bionlpst.BioNLPSTAnnotation;
import org.bibliome.util.bionlpst.BioNLPSTAnnotationVisitor;
import org.bibliome.util.bionlpst.BioNLPSTException;

public abstract class AnnotationSchema implements BioNLPSTAnnotationVisitor<Void,Collection<String>> {
	protected final DocumentSchema documentSchema;
	protected final String type;
	
	protected AnnotationSchema(DocumentSchema documentSchema, String type) throws BioNLPSTException {
		super();
		this.documentSchema = documentSchema;
		this.type = type;
		documentSchema.addAnnotationSchema(this);
	}
	
	public void check(Collection<String> messages, BioNLPSTAnnotation annotation) {
		annotation.accept(this, messages);
	}

	public DocumentSchema getDocumentSchema() {
		return documentSchema;
	}

	public String getType() {
		return type;
	}
	
	protected String unexpectedKind(BioNLPSTAnnotation annotation) {
		return annotation.message("annotations of type " + annotation.getType() + " should be " + getExpectedKind());
	}
	
	protected abstract String getExpectedKind();
	
	abstract Collection<String> getReferencedTypes();
	
	public abstract <R,P> R accept(AnnotationSchemaVisitor<R,P> visitor, P param);
}
