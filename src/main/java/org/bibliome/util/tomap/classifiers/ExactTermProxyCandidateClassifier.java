package org.bibliome.util.tomap.classifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bibliome.util.tomap.Candidate;

public class ExactTermProxyCandidateClassifier extends TermProxyCandidateClassifier {
	public ExactTermProxyCandidateClassifier(String name, Map<Candidate,List<String>> proxies) {
		super(name, proxies);
	}

	@Override
	public List<Attribution> classify(Candidate candidate) {
		if (proxies.containsKey(candidate)) {
			List<String> conceptIDs = proxies.get(candidate);
			List<Attribution> result = new ArrayList<Attribution>(conceptIDs.size());
			for (String conceptID : conceptIDs) {
				Attribution a = new Attribution(this, conceptID, 1);
				result.add(a);
			}
			return result;
		}
		return Collections.emptyList();
	}
}
