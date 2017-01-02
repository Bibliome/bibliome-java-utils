package org.bibliome.util.tomap.classifiers;

import java.util.ArrayList;
import java.util.List;

import org.bibliome.util.tomap.Candidate;

public enum NullCandidateClassifier implements CandidateClassifier {
	INSTANCE {
		@Override
		public List<Attribution> classify(Candidate candidate) {
			return new ArrayList<Attribution>(0);
		}

		@Override
		public String getName() {
			return "null-classifier";
		}
	};
}
