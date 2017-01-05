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
