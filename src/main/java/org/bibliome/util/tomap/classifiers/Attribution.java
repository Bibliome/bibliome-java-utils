/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
