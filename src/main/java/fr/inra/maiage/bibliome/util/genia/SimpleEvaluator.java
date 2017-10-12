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

package fr.inra.maiage.bibliome.util.genia;

/**
 * Recall/precision based evaluator.
 * @author rbossy
 *
 */
public abstract class SimpleEvaluator extends Evaluator {
	private int allPositives = 0;
	private int allTrue = 0;
	private int truePositives = 0;

	@Override
	protected void clear() {
		allPositives = 0;
		allTrue = 0;
		truePositives = 0;
	}
	
	/**
	 * Adds an example.
	 * This method is called by comparing each comparable element and to record enough information to compute recall and precision.
	 * false, false -> true negative (unrecorded)
	 * true, true -> true positive
	 * true, false -> false negative
	 * false, true -> false positive
	 * @param isTrue
	 * @param isPositive
	 */
	protected void example(boolean isTrue, boolean isPositive) {
		if (isTrue)
			allTrue++;
		if (isPositive)
			allPositives++;
		if (isTrue && isPositive)
			truePositives++;
	}

	/**
	 * Returns the number of positive examples.
	 */
	public int getAllPositives() {
		return allPositives;
	}

	/**
	 * Returns the number of true examples.
	 */
	public int getAllTrue() {
		return allTrue;
	}

	/**
	 * Returns the number of true positives.
	 */
	public int getTruePositives() {
		return truePositives;
	}
	
	/**
	 * Returns the recall.
	 */
	public float getRecall() {
		return truePositives / (float)allTrue;
	}
	
	/**
	 * Returns the precision.
	 */
	public float getPrecision() {
		return truePositives / (float)allPositives;
	}
	
	/**
	 * Returns the F-Score.
	 * @param beta
	 */
	public float getFScore(float beta) {
		float recall = getRecall();
		float precision = getPrecision();
		return (1 + beta) * recall * precision / (beta * precision + recall);
	}
}
