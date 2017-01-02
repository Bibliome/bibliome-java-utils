package org.bibliome.util.pairing;

/**
 * Inverts a score.
 * @author rbossy
 *
 * @param <A>
 * @param <B>
 */
public class InvertScore<A, B> implements Score<A,B> {
	private final Score<A,B> score;

	/**
	 * Creates an inversion of the specified score.
	 * @param score
	 */
	public InvertScore(Score<A, B> score) {
		super();
		this.score = score;
	}

	@Override
	public double getScore(A a, B b) {
		return 1 / score.getScore(a, b);
	}
}
