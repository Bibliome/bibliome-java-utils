/**
 * 
 */
package org.bibliome.util.taxonomy.dict;

import java.io.IOException;
import java.util.logging.Logger;

import org.bibliome.util.taxonomy.Name;
import org.bibliome.util.taxonomy.Taxon;

interface TaxonNamePattern {
	void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException;
	boolean isNameRequired();
}