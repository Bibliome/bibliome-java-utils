package org.bibliome.util.taxonomy.reject;

import org.bibliome.util.taxonomy.Name;

/**
 * Reject all names of a given taxon identifier.
 * @author rbossy
 *
 */
public class RejectTaxid implements RejectName {
	private final int taxid;

	/**
	 * Creates a name reject for all names of the specified taxon identifier.
	 * @param taxid
	 */
	public RejectTaxid(int taxid) {
		super();
		this.taxid = taxid;
	}

	@Override
	public boolean reject(int taxid, Name name) {
		return this.taxid == taxid;
	}
}
