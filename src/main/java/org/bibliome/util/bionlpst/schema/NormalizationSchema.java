package org.bibliome.util.bionlpst.schema;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.bionlpst.BioNLPSTAnnotation;
import org.bibliome.util.bionlpst.BioNLPSTException;
import org.bibliome.util.bionlpst.BioNLPSTRelation;
import org.bibliome.util.bionlpst.Event;
import org.bibliome.util.bionlpst.Modification;
import org.bibliome.util.bionlpst.Normalization;
import org.bibliome.util.bionlpst.TextBound;

public class NormalizationSchema extends AnnotationSchema {
	private final Collection<String> types = new HashSet<String>();
	private final Pattern referentPattern;
	
	public NormalizationSchema(DocumentSchema documentSchema, String type, Pattern referentPattern) throws BioNLPSTException {
		super(documentSchema, type);
		this.referentPattern = referentPattern;
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
		BioNLPSTAnnotation annotation = normalization.getAnnotation();
		DocumentSchema.checkType(param, normalization, "annotation", types, annotation);
		String referent = normalization.getReferent();
		Matcher m = referentPattern.matcher(referent);
		if (!m.matches()) {
			String msg = normalization.message("referent " + referent + " does not match control pattern");
			param.add(msg);
		}
		return null;
	}

	@Override
	public Void visit(Modification modification, Collection<String> param) {
		param.add(unexpectedKind(modification));
		return null;
	}

	@Override
	protected String getExpectedKind() {
		return "normalization";
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
