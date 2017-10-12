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

package fr.inra.maiage.bibliome.util.count;

import java.util.Comparator;

/**
 * Occurrence count.
 * @author rbossy
 *
 */
public class Count {
	private long value = 0;
	
	/**
	 * Creates a count with zero occurrences.
	 */
	public Count() {}
	
	/**
	 * Increase the number of occurrences by the specified number.
	 * @param n
	 */
	public void incr(long n) {
		value += n;
	}
	
	/**
	 * Increase the number of occurrences by one.
	 */
	public void incr() {
		value++;
	}
	
	/**
	 * Returns the number of occurrences.
	 */
	public long get() {
		return value;
	}

	/**
	 * Compares count objects by the number of occurrences.
	 * @author rbossy
	 * @param <C>
	 */
	public static class CountComparator<C extends Count> implements Comparator<C> {
		@Override
		public int compare(C a, C b) {
			return Long.compare(a.get(), b.get());
		}
	}
}
