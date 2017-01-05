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

package org.bibliome.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bibliome.util.filelines.TabularFormat;
import org.bibliome.util.filelines.TabularLine;
import org.bibliome.util.streams.SourceStream;

/**
 * Equivalence sets store equivalence between objects.
 * Equivalence is considered commutative and transitive.
 * @author rbossy
 *
 * @param <T>
 */
public abstract class EquivalenceSets<T> {
	private final Map<T,Set<T>> map;

	private EquivalenceSets(Map<T, Set<T>> map) {
		super();
		this.map = map;
	}

	/**
	 * Creates a new equivalence set with an empty hash map.
	 */
	public EquivalenceSets() {
		this(new HashMap<T,Set<T>>());
	}
	
	/**
	 * Factory method to create a new equivalence set.
	 */
	protected abstract Set<T> newSet();
	
	/**
	 * Declares an equivalence between the two specified objects.
	 * @param a
	 * @param b
	 */
	public void setEquivalent(T a, T b) {
		if (a == null)
			return;
		if (b == null)
			return;
		if (map.containsKey(a)) {
			Set<T> aSet = map.get(a);
			if (map.containsKey(b))
				aSet.addAll(map.get(b));
			else
				aSet.add(b);
			map.put(b, aSet);
			return;
		}
		if (map.containsKey(b)) {
			Set<T> bSet = map.get(b);
			bSet.add(a);
			map.put(a, bSet);
			return;
		}
		Set<T> set = newSet();
		set.add(a);
		set.add(b);
		map.put(a, set);
		map.put(b, set);
	}
	
	public void setEquivalent(Collection<? extends T> items) {
		Iterator<? extends T> it = items.iterator();
		if (!it.hasNext())
			return;
		T a = it.next();
		while (it.hasNext())
			setEquivalent(a, it.next());
	}
	
	/**
	 * Returns either the two specified objects are equivalent.
	 * @param a
	 * @param b
	 */
	public boolean isEquivalent(T a, T b) {
		if (map.containsKey(a))
			return map.get(a).contains(b);
		return false;
	}
	
	/**
	 * Returns all objects equivalent to the specified object.
	 * The return value is a collection that contains at least the specified object.
	 * @param a
	 */
	public Collection<T> allEquivalent(T a) {
		if (map.containsKey(a))
			return Collections.unmodifiableCollection(map.get(a));
		return Collections.singleton(a);
	}
	
	
	public T getOneEquivalent(T a) {
		if (map.containsKey(a)) {
			for (T b : map.get(a)) {
				if (!a.equals(b)) {
					return b;
				}
			}
		}
		return null;
	}
	/**
	 * Returns this equivalence sets as a map.
	 */
	public Map<T,Set<T>> getMap() {
		return Collections.unmodifiableMap(map);
	}
	
	public Collection<Set<T>> getSets() {
		return new HashSet<Set<T>>(map.values());
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	public void clear() {
		map.clear();
	}
	
	public static void fill(EquivalenceSets<String> eqs, Iterator<TabularLine> tlit) {
		for (TabularLine tl : Iterators.loop(tlit)) {
			eqs.setEquivalent(tl);
		}
	}
	
	public static void fill(EquivalenceSets<String> eqs, TabularFormat format, BufferedReader reader) {
		fill(eqs, format.iterator(null, reader, true));
	}
	
	public static void fill(EquivalenceSets<String> eqs, TabularFormat format, SourceStream sourceStream) throws IOException {
		fill(eqs, format.iterator(sourceStream, true));
	}
}
