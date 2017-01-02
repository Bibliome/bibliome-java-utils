package org.bibliome.util.tomap.classifiers;

import java.util.Collection;
import java.util.Set;

import org.bibliome.util.tomap.Candidate;
import org.bibliome.util.tomap.Token;

public interface CandidateDistanceFactory {
	CandidateDistance createCandidateDistance(Collection<Candidate> candidates, Set<Token> emptyWords);
}
