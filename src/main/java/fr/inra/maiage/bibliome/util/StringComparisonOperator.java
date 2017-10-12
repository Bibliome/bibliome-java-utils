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

package fr.inra.maiage.bibliome.util;

/**
 * String comparison operators.
 * @author rbossy
 *
 */
public enum StringComparisonOperator {
	EQ {
		@Override
		public boolean accept(String a, String b) {
			return a.equals(b);
		}
	},
	
	NE {
		@Override
		public boolean accept(String a, String b) {
			return !a.equals(b);
		}
	},
	
	CONTAINS {
		@Override
		public boolean accept(String a, String b) {
			return a.contains(b);
		}
	},
	
	STARTS_WITH {
		@Override
		public boolean accept(String a, String b) {
			return a.startsWith(b);
		}
	},
	
	ENDS_WITH {
		@Override
		public boolean accept(String a, String b) {
			return a.endsWith(b);
		}
	};
	
	/**
	 * Returns true iff the comparison is verified for the specified operands.
	 * @param a
	 * @param b
	 */
	public abstract boolean accept(String a, String b);
}
