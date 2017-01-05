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

import java.util.Comparator;
import java.util.List;

import org.bibliome.util.tomap.Candidate;

public interface CandidateDistance {
	double getDistance(Candidate cand1, Candidate cand2);
	List<CandidateMatch> getMatches(Candidate candidate, double threshold);
	
	public static class CandidateMatch {
		private final Candidate candidate;
		private final double distance;
		
		protected CandidateMatch(Candidate candidate, double distance) {
			super();
			this.candidate = candidate;
			this.distance = distance;
		}

		public Candidate getCandidate() {
			return candidate;
		}

		public double getDistance() {
			return distance;
		}
	}
	
	public static final Comparator<CandidateMatch> MATCH_COMPARATOR = new Comparator<CandidateMatch>() {
		@Override
		public int compare(CandidateMatch o1, CandidateMatch o2) {
			return Double.compare(o1.distance, o2.distance);
		}
	};
	
	public static final Comparator<CandidateMatch> REVERSE_MATCH_COMPARATOR = new Comparator<CandidateMatch>() {
		@Override
		public int compare(CandidateMatch o1, CandidateMatch o2) {
			return Double.compare(o2.distance, o1.distance);
		}
	};
}
