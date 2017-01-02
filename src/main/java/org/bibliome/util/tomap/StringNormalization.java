package org.bibliome.util.tomap;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;


public enum StringNormalization {
	NONE {
		@Override
		public String normalize(String s) {
			return s;
		}
	},

	CASE {
		@Override
		public String normalize(String s) {
			return s.toLowerCase();
		}
	},

	DIACRITICS {
		@Override
		public String normalize(String s) {
			return removeDiacritics(s, false);
		}
	},
	
	CASE_AND_DIACRITICS {
		@Override
		public String normalize(String s) {
			return removeDiacritics(s, true);
		}
	};

	@SuppressWarnings("serial")
	private static final Set<Character.UnicodeBlock> DIACRITICS_BLOCKS = new HashSet<Character.UnicodeBlock>() {
		{
			add(Character.UnicodeBlock.COMBINING_DIACRITICAL_MARKS);
			add(Character.UnicodeBlock.COMBINING_DIACRITICAL_MARKS_SUPPLEMENT);
		}
	};

	private static String removeDiacritics(String s, boolean lowerCase) {
		String nfd = Normalizer.normalize(s, Normalizer.Form.NFD);
		StringBuilder sb = new StringBuilder(nfd.length());
		for (int i = 0; i < nfd.length(); ++i) {
			char c = nfd.charAt(i);
			Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
			if (!DIACRITICS_BLOCKS.contains(block)) {
				if (lowerCase) {
					c = Character.toLowerCase(c);
				}
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public abstract String normalize(String s);
	
	public String normalizeNull(String s) {
		if (s == null) {
			return null;
		}
		return normalize(s);
	}

	public static StringNormalization get(boolean lowerCase, boolean removeDiacritics) {
		if (lowerCase) {
			if (removeDiacritics) {
				return CASE_AND_DIACRITICS;
			}
			return CASE;
		}
		if (removeDiacritics) {
			return DIACRITICS;
		}
		return NONE;
	}
}
