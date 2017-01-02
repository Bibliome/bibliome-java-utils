package org.bibliome.util.yatea.pattern;

public enum WordAttribute {
	POS,
	LEMMA;
	
	public static WordAttribute get(String s) {
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if (Character.isLowerCase(c)) {
				return  WordAttribute.LEMMA;
			}
		}
		return WordAttribute.POS;
	}
}