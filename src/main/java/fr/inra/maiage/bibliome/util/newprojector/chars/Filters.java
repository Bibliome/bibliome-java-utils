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

package fr.inra.maiage.bibliome.util.newprojector.chars;

import fr.inra.maiage.bibliome.util.newprojector.CharFilter;

/**
 * Standard character filters.
 * @author rbossy
 *
 */
public enum Filters implements CharFilter {
	/**
	 * Match all input characters.
	 */
	ACCEPT_ALL {
		@Override
		public boolean accept(int last, char c) {
			return true;
		}
		
		@Override
		public CharFilter combine(CharFilter cf) {
			return cf;
		}
	},

	/**
	 * Match no character input.
	 */
	REJECT_ALL {
		@Override
		public boolean accept(int last, char c) {
			return false;
		}
		
		@Override
		public CharFilter combine(CharFilter cf) {
			return REJECT_ALL;
		}
	},
	
	/**
	 * Match only the first character of the input.
	 */
	FIRST {
		@Override
		public boolean accept(int last, char c) {
			return last == -1;
		}
	},
	
	/**
	 * Match only the first character of a word.
	 */
	START_WORD {
		@Override
		public boolean accept(int last, char c) {
			if (Character.isWhitespace(c)) {
				return false; 
			}
			if (last == -1)
				return true;
			return !Character.isLetterOrDigit((char) last);
		}
	},
	
	/**
	 * Match only the last character of words.
	 */
	END_WORD {
		@Override
		public boolean accept(int last, char c) {
			return !Character.isLetterOrDigit(c);
		}
	},
	
	/**
	 * Do not match whitespace characters.
	 */
	NO_SPACE {
		@Override
		public boolean accept(int last, char c) {
			return !Character.isWhitespace(c);
		}
	},
	
	/**
	 * Match only one of consecutive whitespace characters. 
	 */
	NORM_SPACE {
		@Override
		public boolean accept(int last, char c) {
			if (Character.isWhitespace(c)) {
				if (last == -1)
					return false;
				if (Character.isWhitespace((char) last))
					return false;
			}
			return true;
		}
	};
	
	@Override
	public CharFilter combine(CharFilter cf) {
		return new MultiFilter(this, cf);
	}
}
