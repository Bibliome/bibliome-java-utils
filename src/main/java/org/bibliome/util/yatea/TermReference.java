package org.bibliome.util.yatea;

import java.util.Map;

public interface TermReference {
	String getId();
	boolean isMnp();
	String getForm();
	String getLemma();
	String getSyntacticCategory();
	TermId getHead();
	int getnOccurrences();
	float getConfidence();
	TermId getSaHead();
	TermId getSaModifier();
	ModifierPosition getSaPosition();
	String getSaPrep();
	void resolveTermIds(Map<String,TermCandidate> candidatesCollection);
}