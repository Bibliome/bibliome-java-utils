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
