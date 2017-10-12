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
import java.util.Collections;

import fr.inra.maiage.bibliome.util.newprojector.State;

/**
 * State for dictionaries that only hold one entry per key.
 * @author rbossy
 *
 * @param <T>
 */
public abstract class SingleValueState<T> extends State<T> {
	protected T value = null;

	/**
	 * Creates a new single value state.
	 */
	protected SingleValueState() {
		super();
	}

	/**
	 * Creates a single value state holding with the specified character and parent state.
	 * @param c
	 * @param parent
	 */
	protected SingleValueState(char c, State<T> parent) {
		super(c, parent);
	}

	@Override
	public Collection<T> getValues() {
		if (value == null)
			return Collections.emptyList();
		return Collections.singletonList(value);
	}

	@Override
	public boolean hasValue() {
		return value != null;
	}
}
