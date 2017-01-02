package org.bibliome.util.yatea;

import java.util.Map;

public class TermId implements TermReference {
	private final String id;
	private TermCandidate candidate;

	public TermId(String id) {
		super();
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isMnp() {
		return getCandidate().isMnp();
	}

	@Override
	public String getForm() {
		return getCandidate().getForm();
	}

	@Override
	public String getLemma() {
		return getCandidate().getLemma();
	}

	@Override
	public String getSyntacticCategory() {
		return getCandidate().getSyntacticCategory();
	}

	@Override
	public TermId getHead() {
		return getCandidate().getHead();
	}

	@Override
	public int getnOccurrences() {
		return getCandidate().getnOccurrences();
	}

	@Override
	public float getConfidence() {
		return getCandidate().getConfidence();
	}

	@Override
	public TermId getSaHead() {
		return getCandidate().getSaHead();
	}

	@Override
	public TermId getSaModifier() {
		return getCandidate().getSaModifier();
	}

	@Override
	public ModifierPosition getSaPosition() {
		return getCandidate().getSaPosition();
	}

	@Override
	public String getSaPrep() {
		return getCandidate().getSaPrep();
	}

	@Override
	public int hashCode() {
		return getCandidate().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return getCandidate().equals(obj);
	}

	@Override
	public String toString() {
		if (candidate == null) {
			return "REF:" + id;
		}
		return candidate.toString();
	}

	public TermCandidate getCandidate() {
		if (candidate == null) {
			throw new RuntimeException("unresolved term identifier");
		}
		return candidate;
	}

	@Override
	public void resolveTermIds(Map<String,TermCandidate> candidatesCollection) {
		if (candidate != null) {
			return;
		}
		if (!candidatesCollection.containsKey(id)) {
			throw new RuntimeException("cannot resolve term id: '" + id + "'");
		}
		candidate = candidatesCollection.get(id);
	}
}
