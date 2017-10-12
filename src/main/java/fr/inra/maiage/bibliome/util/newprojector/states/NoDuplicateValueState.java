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

package fr.inra.maiage.bibliome.util.newprojector.states;

import java.util.Collection;
import java.util.LinkedHashSet;

import fr.inra.maiage.bibliome.util.newprojector.State;

/**
 * State for dictionaries holding multiple values for a key but no duplicate entries.
 * @author rbossy
 *
 * @param <T>
 */
public class NoDuplicateValueState<T> extends MultipleValuesState<T> {
	/**
	 * Creates a root no duplicate value state.
	 */
	public NoDuplicateValueState() {
		super();
	}

	private NoDuplicateValueState(char c, State<T> parent) {
		super(c, parent);
	}

	@Override
	protected Collection<T> newCollection() {
		return new LinkedHashSet<T>(2);
	}

	@Override
	protected State<T> newState(char c, State<T> parent) {
		return new NoDuplicateValueState<T>(c, parent);
	}
}
