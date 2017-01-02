package org.bibliome.util.pairing;

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
