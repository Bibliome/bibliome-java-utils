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

import java.util.HashMap;
import java.util.Map;

import org.bibliome.util.defaultmap.DefaultMap;

/**
 * Stats for counting terms occurrences and term document occurrences.
 * @author rbossy
 *
 * @param <K> Document type
 */
public class TfIdfDocuments<K> extends DefaultMap<K,TfIdfStats> {
	private final CountStats<String> total = new CountStats<String>(new HashMap<String,Count>());
	
	/**
	 * Creates a new TF-IDF document stats supported by the specified map.
	 * @param map
	 */
	public TfIdfDocuments(Map<K,TfIdfStats> map) {
		super(true, map);
	}

	@Override
	protected TfIdfStats defaultValue(K key) {
		return new TfIdfStats();
	}

	private CountStats<String> getDF() {
		CountStats<String> result = new CountStats<String>(new HashMap<String,Count>());
		for (TfIdfStats doc : values())
			for (String term : doc.keySet())
				result.incr(term);
		return result;
	}

	/**
	 * Compute the TF-IDF of each term.
	 * Calling this method only makes sense when counting is finished.
	 */
	public void compute() {
		int nDocs = size();
		CountStats<String> df = getDF();
		for (TfIdfStats doc : values()) {
			for (Map.Entry<String,TfIdf> e : doc.entrySet()) {
				e.getValue().compute(doc.sum(), nDocs, df.safeGet(e.getKey()).get());
			}
		}
	}
	
	/**
	 * Increments the specified document/term pair by one.
	 * @param doc
	 * @param term
	 */
	public void incr(K doc, String term) {
		safeGet(doc).incr(term);
		total.incr(term);
	}
	
	/**
	 * Returns a stats file that associates each term to its total number of occurrences.
	 */
	public CountStats<String> getTotal() {
		return total;
	}
}
