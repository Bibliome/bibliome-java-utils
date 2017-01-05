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

package org.bibliome.util.newprojector.chars;

import java.text.Normalizer;

import org.bibliome.util.newprojector.CharMapper;

/**
 * Standard character mappers.
 * @author rbossy
 *
 */
public enum Mappers implements CharMapper {
	/**
	 * Exact match.
	 */
	IDENTITY {
		@Override
		public char map(int last, char c) {
			return c;
		}
		
		@Override
		public CharMapper combine(CharMapper cm) {
			return cm;
		}
	},
	
	/**
	 * Lower case folding.
	 */
	TO_LOWER {
		@Override
		public char map(int last, char c) {
			return Character.toLowerCase(c);
		}
	},
	
	/**
	 * Upper case folding.
	 */
	TO_UPPER {
		@Override
		public char map(int last, char c) {
			return Character.toUpperCase(c);
		}
	},
	
	/**
	 * Normalize diacritics.
	 */
	NO_DIACRITICS {
		@Override
		public char map(int last, char c) {
			return Normalizer.normalize(Character.toString(c), Normalizer.Form.NFD).charAt(0);
		}
	},
	
	/**
	 * Transform whitespace character into a space character.
	 */
	NORM_SPACE {
		@Override
		public char map(int last, char c) {
			if (Character.isWhitespace(c))
				return ' ';
			return c;
		}
	};
	
	@Override
	public CharMapper combine(CharMapper cm) {
		return new MultiMapper(this, cm);
	}
}
