package org.bibliome.util.tomap.classifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibliome.util.tomap.Candidate;
import org.bibliome.util.tomap.Token;
import org.bibliome.util.tomap.classifiers.CandidateDistance.CandidateMatch;

public class ScoreBasedTermProxyCandidateClassifier extends TermProxyCandidateClassifier {
	private final CandidateDistance distance;
	private final double threshold;

	public ScoreBasedTermProxyCandidateClassifier(String name, Map<Candidate,List<String>> proxies, CandidateDistance distance, double threshold) {
		super(name, proxies);
		this.distance = distance;
		this.threshold = threshold;
	}

	public ScoreBasedTermProxyCandidateClassifier(String name, Map<Candidate,List<String>> proxies, CandidateDistanceFactory distanceFactory, double threshold, Set<Token> emptyWords) {
		this(name, proxies, distanceFactory.createCandidateDistance(proxies.keySet(), emptyWords), threshold);
	}

	@Override
	public List<Attribution> classify(Candidate candidate) {
		List<Attribution> result = new ArrayList<Attribution>();
		for (CandidateMatch m : distance.getMatches(candidate, threshold)) {
			for (String conceptID : proxies.get(m.getCandidate())) {
				Attribution a = new Attribution(this, conceptID, m.getDistance());
				result.add(a);
			}
		}
		return result;
	}
}
