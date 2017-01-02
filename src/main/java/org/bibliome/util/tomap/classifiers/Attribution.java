package org.bibliome.util.tomap.classifiers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Attribution {
	public static final String CLASSIFIER = "classifier";

	private final String conceptID;
	private final double score;
	private final Map<String,String> explanations = new HashMap<String,String>();
	
	public Attribution(CandidateClassifier classifier, String conceptID, double score) {
		super();
		this.conceptID = conceptID;
		this.score = score;
		setClassifierExplanation(classifier);
	}
	
	public void setClassifierExplanation(CandidateClassifier classifier) {
		this.explanations.put(CLASSIFIER, classifier.getName());
	}

	public String getConceptID() {
		return conceptID;
	}

	public double getScore() {
		return score;
	}

	public Collection<String> getExplanationKeys() {
		return Collections.unmodifiableCollection(explanations.keySet());
	}
	
	public boolean hasExplanation(String key) {
		return explanations.containsKey(key);
	}
	
	public String getExplanation(String key) {
		return explanations.get(key);
	}
	
	public void setExplanation(String key, String value) {
		explanations.put(key, value);
	}
}
