package org.bibliome.util.tomap.classifiers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibliome.util.defaultmap.DefaultArrayListHashMap;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.tomap.Candidate;
import org.bibliome.util.tomap.Token;
import org.bibliome.util.tomap.TokenToStringMapper;

public class HeadBasedTermProxyCandidateClassifier extends TermProxyCandidateClassifier {
	private final Set<Token> headGraylist;
	private final Map<Token,List<Candidate>> heads;
	private final CandidateDistance candidateDistance;
	private final boolean wholeCandidateDistance;
	private boolean candidateHeadPriority;

	public HeadBasedTermProxyCandidateClassifier(String name, Map<Candidate,List<String>> proxies, Set<Token> headGraylist, CandidateDistance candidateDistance, boolean wholeCandidateDistance, boolean wholeProxyDistance, boolean candidateHeadPriority, boolean proxyHeadPriority) {
		super(name, proxies);
		this.headGraylist = headGraylist;
		DefaultMap<Token,List<Candidate>> heads = new DefaultArrayListHashMap<Token,Candidate>();
		for (Candidate cand : proxies.keySet()) {
			Collection<Candidate> significantParts = getSignificantParts(cand, proxyHeadPriority);
			for (Candidate sign : significantParts) {
				Token significantHead = sign.getHeadToken();
				heads.safeGet(significantHead).add(wholeProxyDistance ? cand : sign);
			}
		}
		this.heads = heads;
		this.candidateDistance = candidateDistance;
		this.wholeCandidateDistance = wholeCandidateDistance;
		this.candidateHeadPriority = candidateHeadPriority;
	}

	public HeadBasedTermProxyCandidateClassifier(String name, Map<Candidate,List<String>> proxies, Set<Token> headGraylist, CandidateDistanceFactory candidateDistanceFactory, Set<Token> emptyWords, boolean wholeCandidateDistance, boolean wholeProxyDistance, boolean candidateHeadPriority, boolean proxyHeadPriority)	{
		this(name, proxies, headGraylist, candidateDistanceFactory.createCandidateDistance(proxies.keySet(), emptyWords), wholeCandidateDistance, wholeProxyDistance, candidateHeadPriority, proxyHeadPriority);
	}

	public Collection<Candidate> getSignificantParts(Candidate candidate, boolean headPriority) {
		Collection<Candidate> result = new ArrayList<Candidate>(2);
		getSignificantParts(candidate, headPriority, result);
		System.err.println(candidate + " -> " + result);
		return result;
	}

	private boolean getSignificantParts(Candidate candidate, boolean headPriority, Collection<Candidate> significantParts) {
		Token headToken = candidate.getHeadToken();
		if (!headGraylist.contains(headToken)) {
			significantParts.add(candidate);
			return true;
		}
		boolean result = false;
		Candidate head = candidate.getHead();
		if (head != null) {
			result = getSignificantParts(head, headPriority, significantParts);
			if (result && headPriority) {
				return result;
			}
		}
		for (Candidate mod : candidate.getModifiers()) {
			result = getSignificantParts(mod, headPriority, significantParts) || result;
		}
		return result;
	}

	@Override
	public List<Attribution> classify(Candidate candidate) {
		Collection<Candidate> significantParts = getSignificantParts(candidate, candidateHeadPriority);
		if (significantParts.isEmpty()) {
			return Collections.emptyList();
		}
		List<Attribution> result = new ArrayList<Attribution>();
		Token tokenHead = candidate.getHeadToken();
		for (Candidate sign : significantParts) {
			Token significantHead = sign.getHeadToken();
			if (heads.containsKey(significantHead)) {
				List<Candidate> knownSynonyms = heads.get(significantHead);
				for (Candidate syn : knownSynonyms) {
					if (!proxies.containsKey(syn)) {
						continue;
					}
					String synForm = syn.getString(TokenToStringMapper.FORM);
					List<String> concepts = proxies.get(syn);
					for (String conceptID : concepts) {
						double score = candidateDistance.getDistance(syn, wholeCandidateDistance ? candidate : sign);
						Attribution a = new Attribution(this, conceptID, score);
						a.setExplanation("concept-synonym", synForm);
						a.setExplanation("significant-head", significantHead.getForm());
						a.setExplanation("candidate-head", tokenHead.getForm());
						result.add(a);
					}
				}
			}
		}
		return result;
	}
}
