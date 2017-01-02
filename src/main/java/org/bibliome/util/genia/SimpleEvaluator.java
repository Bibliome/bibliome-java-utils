package org.bibliome.util.genia;

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
