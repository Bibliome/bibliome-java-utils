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

import fr.inra.maiage.bibliome.util.newprojector.State;

/**
 * State for dictionaries that only hold one entry per key.
 * If several entries are inserted for the same key, then only the first inserted is kept.
 * @author rbossy
 *
 * @param <T>
 */
public class FirstValueState<T> extends SingleValueState<T> {
	/**
	 * Creates a root first value state.
	 */
	public FirstValueState() {
		super();
	}

	private FirstValueState(char c, State<T> parent) {
		super(c, parent);
	}

	@Override
	protected void addValue(T value, CharSequence key) {
		if (this.value == null)
			this.value = value;
	}

	@Override
	protected State<T> newState(char c, State<T> parent) {
		return new FirstValueState<T>(c, parent);
	}
}
