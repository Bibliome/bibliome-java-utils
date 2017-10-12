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

package fr.inra.maiage.bibliome.util.tomap.classifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.util.tomap.Candidate;

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
