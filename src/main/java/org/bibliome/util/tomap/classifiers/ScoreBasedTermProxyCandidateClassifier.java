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
