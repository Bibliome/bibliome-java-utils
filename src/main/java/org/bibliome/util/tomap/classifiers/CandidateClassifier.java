package org.bibliome.util.tomap.classifiers;

import java.util.List;

import org.bibliome.util.tomap.Candidate;

public interface CandidateClassifier {
	List<Attribution> classify(Candidate candidate);
	String getName();
}
