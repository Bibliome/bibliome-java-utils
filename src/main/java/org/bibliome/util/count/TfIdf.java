package org.bibliome.util.count;

import java.util.Comparator;

/**
 * tf-idf count.
 * @author rbossy
 *
 */
public class TfIdf extends Count {
	private double tf;
	private double idf;
	private double tfidf;
	
	/**
	 * Returns the TF.
	 */
	public double getTf() {
		return tf;
	}

	/**
	 * Returns the IDF.
	 */
	public double getIdf() {
		return idf;
	}

	/**
	 * Returns the TF-IDF.
	 */
	public double getTfIdf() {
		return tfidf;
	}
	
	/**
	 * Computes the TF and IDF according to the specified document size, number of documents and document count.
	 * @param docSize
	 * @param nDocs
	 * @param termDocs
	 */
	public void compute(long docSize, int nDocs, long termDocs) {
		tf = get() / (float) docSize;
		idf = Math.log(nDocs / (1.0 + termDocs));
		tfidf = tf * idf;
	}

	/**
	 * Compares two TfIdf objects by TF-IDF score.
	 * @author rbossy
	 *
	 * @param <C>
	 */
	public static class TfIdfComparator<C extends TfIdf> implements Comparator<C> {
		@Override
		public int compare(C a, C b) {
			return Double.compare(a.getTfIdf(), b.getTfIdf());
		}
	}
}
