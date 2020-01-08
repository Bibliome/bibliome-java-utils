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

package fr.inra.maiage.bibliome.util.taxonomy.reject;

import fr.inra.maiage.bibliome.util.taxonomy.Name;

/**
 * Reject all names of a given taxon identifier.
 * @author rbossy
 *
 */
public class RejectTaxid implements RejectName {
	private final String taxid;

	/**
	 * Creates a name reject for all names of the specified taxon identifier.
	 * @param taxid
	 */
	public RejectTaxid(String taxid) {
		super();
		this.taxid = taxid;
	}

	@Override
	public boolean reject(String taxid, Name name) {
		return this.taxid.equals(taxid);
	}
}
