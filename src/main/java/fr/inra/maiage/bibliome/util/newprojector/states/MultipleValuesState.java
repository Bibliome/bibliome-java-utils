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
 * State for dictionaries that can hold several entries for one key.
 * @author rbossy
 *
 * @param <T>
 */
public abstract class MultipleValuesState<T> extends State<T> {
	private Collection<T> values = null;
	
	/**
	 * Creates a root multiple values state.
	 */
	protected MultipleValuesState() {
		super();
	}

	/**
	 * Creates a multiple values state with the specified character and parent state.
	 * @param c
	 * @param parent
	 */
	protected MultipleValuesState(char c, State<T> parent) {
		super(c, parent);
	}

	@Override
	protected void addValue(T value, CharSequence key) {
		if (values == null)
			values = newCollection();
		values.add(value);
	}
	
	/**
	 * Creates an entry collection.
	 */
	protected abstract Collection<T> newCollection();

	@Override
	public Collection<T> getValues() {
		return Collections.unmodifiableCollection(values);
	}

	@Override
	public boolean hasValue() {
		return values != null;
	}
}
