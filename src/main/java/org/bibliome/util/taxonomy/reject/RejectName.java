package org.bibliome.util.taxonomy.reject;

import org.bibliome.util.taxonomy.Name;

/**
 * Taxon name reject.
 * @author rbossy
 *
 */
public interface RejectName {
	/**
	 * Returns either to reject the specified name of the specified taxon identifier.
	 * @param taxid
	 * @param name
	 */
	boolean reject(int taxid, Name name);
}
