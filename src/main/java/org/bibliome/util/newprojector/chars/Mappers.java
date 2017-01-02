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
