package org.bibliome.util.pairing;

/**
 * Score between two objects.
 * @author rbossy
 *
 * @param <A>
 * @param <B>
 */
public interface Score<A, B> {
	double getScore(A a, B b);
}
