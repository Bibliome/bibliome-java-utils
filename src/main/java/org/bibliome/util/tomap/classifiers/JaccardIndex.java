package org.bibliome.util.tomap.classifiers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibliome.util.defaultmap.DefaultArrayListHashMap;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.tomap.Candidate;
import org.bibliome.util.tomap.Token;

public class JaccardIndex implements CandidateDistance {
	private final Map<Token,List<Candidate>> index;
	private final Set<Token> emptyWords;

	JaccardIndex(Collection<Candidate> candidates, Set<Token> emptyWords) {
		DefaultMap<Token,List<Candidate>> index = new DefaultArrayListHashMap<Token,Candidate>();
		for (Candidate cand : candidates) {
			for (Token t : cand.getTokens()) {
				if (!emptyWords.contains(t)) {
					index.safeGet(t).add(cand);
				}
			}
		}
		this.index = index;
		this.emptyWords = emptyWords;
	}

	@Override
	public double getDistance(Candidate cand1, Candidate cand2) {
		Set<Token> set1 = new HashSet<Token>(cand1.getTokens());
		set1.removeAll(emptyWords);
		double union = set1.size();
		double intersection = 0;
		for (Token t : cand2.getTokens()) {
			if (!emptyWords.contains(t)) {
				if (set1.contains(t)) {
					intersection++;
				}
				else {
					union++;
				}
			}
		}
		return intersection / union;
	}
	
	public Collection<Candidate> getUnscoredMatches(Candidate candidate) {
		Set<Candidate> result = new HashSet<Candidate>();
		for (Token t : candidate.getTokens()) {
			if (index.containsKey(t)) {
				result.addAll(index.get(t));
			}
		}
		return result;
	}

	@Override
	public List<CandidateMatch> getMatches(Candidate candidate, double threshold) {
		Collection<Candidate> matches = getUnscoredMatches(candidate);
		List<CandidateMatch> result = new ArrayList<CandidateMatch>(matches.size());
		for (Candidate match : matches) {
			double distance = getDistance(match, candidate);
			if (distance >= threshold) {
				CandidateMatch m = new CandidateMatch(match, distance);
				result.add(m);
			}
		}
		Collections.sort(result, REVERSE_MATCH_COMPARATOR);
		return result;
	}
}
