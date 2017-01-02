/**
 * 
 */
package org.bibliome.util.taxonomy.dict;

import java.io.IOException;
import java.util.logging.Logger;

import org.bibliome.util.taxonomy.Name;
import org.bibliome.util.taxonomy.Taxon;

final class ConstantPattern implements TaxonNamePattern {
	private final String value;

	ConstantPattern(String value) {
		super();
		this.value = value;
	}

	@Override
	public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
		target.append(value);
	}

	@Override
	public boolean isNameRequired() {
		return false;
	}
}