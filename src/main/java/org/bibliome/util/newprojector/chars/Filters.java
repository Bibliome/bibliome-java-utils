package org.bibliome.util.newprojector.chars;

import org.bibliome.util.newprojector.CharFilter;

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
