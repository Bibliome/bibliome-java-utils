package org.bibliome.util.yatea.pattern;

public enum SubtermRole {
	NONE,
	MODIFIER,
	HEAD;

	public static SubtermRole get(char c) {
		switch (c) {
			case 'M': return MODIFIER;
			case 'H': return HEAD;
		}
		throw new RuntimeException();
	}
}