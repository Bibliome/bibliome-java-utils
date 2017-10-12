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

package fr.inra.maiage.bibliome.util.pairing;

import java.util.Collection;
import java.util.List;

/**
 * Affine-gap alignment score function.
 * The getScore() methods indicates the (mis)match score.
 * @author rbossy
 *
 * @param <A>
 * @param <B>
 */
public abstract class AlignmentScore<A,B> implements Score<A,B> {
	private final double initialScore;

	/**
	 * Creates a new alignment score with the specified initial value.
	 * @param initialScore
	 */
	protected AlignmentScore(double initialScore) {
		super();
		this.initialScore = initialScore;
	}

	/**
	 * Returns the value of the top-left cell.
	 */
	public double initialScore() {
		return initialScore;
	}

	/**
	 * Returns the gap score.
	 */
	public abstract double getGap();
	
	/**
	 * Computes the score according to specified previous and pair scores.
	 * @param prev
	 * @param score
	 * @return the score.
	 */
	public abstract double compute(double prev, double score);
	
	/**
	 * Combines the three incoming scores.
	 * @param all 3-element collection of scores
	 * @param best best of scores
	 * @return the score combination.
	 */
	public abstract double combine(Collection<Double> all, double best);
	
	/**
	 * This alignment score as a score function between two sequences.
	 */
	public final Score<List<A>,List<B>> SCORE = new Score<List<A>,List<B>>() {
		@Override
		public double getScore(List<A> a, List<B> b) {
			NeedlemanWunsch<A,B> nw = new NeedlemanWunsch<A,B>(a, b, AlignmentScore.this);
			nw.solve();
			return nw.getScore();
		}
	};
}
