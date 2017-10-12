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

import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.taxonomy.Name;

/**
 * Name reject that reject names rejected by both components.
 * @author rbossy
 *
 */
public class RejectConjunction extends Pair<RejectName,RejectName> implements RejectName {
	/**
	 * Creates a name reject that rejects names rejected by both specified components.
	 * @param first
	 * @param second
	 */
	public RejectConjunction(RejectName first, RejectName second) {
		super(first, second);
	}

	@Override
	public boolean reject(int taxid, Name name) {
		return first.reject(taxid, name) && second.reject(taxid, name);
	}
}
