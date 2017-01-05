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
