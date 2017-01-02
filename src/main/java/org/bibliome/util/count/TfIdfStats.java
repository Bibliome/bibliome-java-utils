/**
 * 
 */
package org.bibliome.util.count;

import java.util.HashMap;

/**
 * Stats for computing terms TF-IDF.
 * @author rbossy
 *
 */
public class TfIdfStats extends Stats<String,TfIdf> {
	/**
	 * Creates an empty TF-IDF stats supported by a hash map.
	 */
	public TfIdfStats() {
		super(new HashMap<String,TfIdf>());
	}

	@Override
	protected TfIdf defaultValue(String key) {
		return new TfIdf();
	}	
}
