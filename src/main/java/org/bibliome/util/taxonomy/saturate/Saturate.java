package org.bibliome.util.taxonomy.saturate;

import java.util.Collection;

import org.bibliome.util.taxonomy.Name;

/**
 * Saturates a taxon name.
 * @author rbossy
 *
 */
public interface Saturate {
	/**
	 * Returns all names to add generated from the specified source name.
	 * @param name
	 */
	Collection<Name> saturate(Name name);
}
