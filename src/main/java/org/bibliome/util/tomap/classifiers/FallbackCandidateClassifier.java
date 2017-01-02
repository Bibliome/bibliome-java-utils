package org.bibliome.util.tomap.classifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bibliome.util.tomap.Candidate;

public class FallbackCandidateClassifier extends AbstractCandidateClassifier {
	private final List<CandidateClassifier> classifiers = new ArrayList<CandidateClassifier>();

	public FallbackCandidateClassifier(String name) {
		super(name);
	}

	public void addClassifier(CandidateClassifier classifier) {
		classifiers.add(classifier);
	}

	@Override
	public List<Attribution> classify(Candidate candidate) {
		for (CandidateClassifier classifier : classifiers) {
			List<Attribution> attrs = classifier.classify(candidate);
			if (!attrs.isEmpty()) {
				return attrs;
			}
		}
		return Collections.emptyList();
	}
}
