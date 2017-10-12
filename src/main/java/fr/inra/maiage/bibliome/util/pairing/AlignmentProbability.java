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

/**
 * Alignment probability function.
 * This class relies on the fact that getScore() and getGap() return valid probability values.
 * 
 * @author rbossy
 *
 * @param <A>
 * @param <B>
 */
public abstract class AlignmentProbability<A,B> extends AlignmentScore<A,B> {
	private final boolean total;

	/**
	 * Creates a new alignment probability function.
	 * Initial value is 1.
	 * @param total either to keep total alignment probability (if false, the keeps best alignment probability)
	 */
	protected AlignmentProbability(boolean total) {
		super(1);
		this.total = total;
	}

	/**
	 * Returns prev * score.
	 */
	@Override
	public double compute(double prev, double score) {
		return prev * score;
	}

	/**
	 * If total is true, then returns the sum of all, otherwise returns best.
	 */
	@Override
	public double combine(Collection<Double> all, double best) {
		if (total) {
			double result = 0;
			for (double d : all)
				result += d;
			return result;
		}
		return best;
	}
}
