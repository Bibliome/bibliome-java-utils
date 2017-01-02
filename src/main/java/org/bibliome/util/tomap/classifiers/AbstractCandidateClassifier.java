package org.bibliome.util.tomap.classifiers;


public abstract class AbstractCandidateClassifier implements CandidateClassifier {
	private final String name;

	protected AbstractCandidateClassifier(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
