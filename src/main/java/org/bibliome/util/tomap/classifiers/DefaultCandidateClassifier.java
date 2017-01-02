package org.bibliome.util.tomap.classifiers;

import java.util.Collections;
import java.util.List;

import org.bibliome.util.tomap.Candidate;

public class DefaultCandidateClassifier extends AbstractCandidateClassifier {
	private final String conceptID;
	
	public DefaultCandidateClassifier(String name, String conceptID) {
		super(name);
		this.conceptID = conceptID;
	}

	@Override
	public List<Attribution> classify(Candidate candidate) {
		Attribution attr = new Attribution(this, conceptID, 1);
		return Collections.singletonList(attr);
	}
}
