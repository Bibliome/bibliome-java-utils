package org.bibliome.util.bionlpst;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

public abstract class BioNLPSTAnnotation extends Sourced {
	private final String id;
	private final String type;
	private final Collection<Modification> modifications = new LinkedHashSet<Modification>();
	private final Collection<Normalization> normalizations = new LinkedHashSet<Normalization>();

	protected BioNLPSTAnnotation(String source, int lineno, BioNLPSTDocument document, Visibility visibility, String id, String type) throws BioNLPSTException {
		super(source, lineno, document, visibility);
		this.id = id;
		this.type = type;
		document.addAnnotation(this);
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}
	
	public Collection<Modification> getModifications() {
		return Collections.unmodifiableCollection(modifications);
	}

	public Collection<Normalization> getNormalizations() {
		return Collections.unmodifiableCollection(normalizations);
	}
	
	void addModification(Modification mod) {
		modifications.add(mod);
	}
	
	void addNormalization(Normalization norm) {
		normalizations.add(norm);
	}

	public abstract void resolveIds() throws BioNLPSTException;
	
	public abstract AnnotationKind getKind();
	
	public abstract <R,P> R accept(BioNLPSTAnnotationVisitor<R,P> visitor, P param);
}
