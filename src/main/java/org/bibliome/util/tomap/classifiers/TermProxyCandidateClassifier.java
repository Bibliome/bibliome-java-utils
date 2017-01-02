package org.bibliome.util.tomap.classifiers;

import java.util.List;
import java.util.Map;

import org.bibliome.util.tomap.Candidate;

public abstract class TermProxyCandidateClassifier extends AbstractCandidateClassifier {
	protected final Map<Candidate,List<String>> proxies;

	protected TermProxyCandidateClassifier(String name, Map<Candidate,List<String>> proxies) {
		super(name);
		this.proxies = proxies;
	}
}
