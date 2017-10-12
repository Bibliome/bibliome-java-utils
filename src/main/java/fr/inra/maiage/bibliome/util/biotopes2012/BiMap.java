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

package fr.inra.maiage.bibliome.util.biotopes2012;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.inra.maiage.bibliome.util.Pair;

/**
 * Bijective map based on HashMap.
 * @author rbossy
 *
 * @param <T>
 * @param <U>
 */
class BiMap<T,U> {
	private final Map<T,U> forth = new HashMap<T,U>();
	private final Map<U,T> back = new HashMap<U,T>();
	
	/**
	 * Creates a bi-map.
	 */
	public BiMap() {
	}
	
	public BiMap(Map<? extends T,? extends U> map) {
		for (Map.Entry<? extends T,? extends U> e : map.entrySet())
			add(e.getKey(), e.getValue());
	}
	
	public BiMap(Collection<Pair<? extends T,? extends U>> c) {
		for (Pair<? extends T,? extends U> pair : c)
			add(pair);
	}
	
	public U getForth(T t) {
		return forth.get(t);
	}
	
	public T getBack(U u) {
		return back.get(u);
	}
	
	public void add(T t, U u) {
		if (t == null)
			throw new NullPointerException();
		if (u == null)
			throw new NullPointerException();
		forth.remove(t);
		back.remove(u);
		forth.put(t, u);
		back.put(u, t);
	}
	
	public void add(Pair<? extends T, ? extends U> pair) {
		add(pair.first, pair.second);
	}
	
	public boolean hasPair(T t, U u) {
		return forth.containsKey(t) && forth.get(t).equals(u);
	}
}
