package org.bibliome.util.pairing;

import java.util.Collection;

/**
 * Classic alignment score.
 * @author rbossy
 *
 * @param <A>
 * @param <B>
 */
public abstract class ClassicAlignmentScore<A,B> extends AlignmentScore<A,B> {
	/**
	 * Creates a new classic alignment score with the specified initial value.
	 * @param initialScore
	 */
	protected ClassicAlignmentScore(double initialScore) {
		super(initialScore);
	}

	/**
	 * Creates a new classic alignment score with initial value 0.
	 */
	protected ClassicAlignmentScore() {
		this(0);
	}

	/**
	 * Returns prev + score.
	 */
	@Override
	public double compute(double prev, double score) {
		return prev + score;
	}

	/**
	 * Returns best.
	 */
	@Override
	public double combine(Collection<Double> all, double best) {
		return best;
	}
}
