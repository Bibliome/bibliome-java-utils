package org.bibliome.util;

/**
 * String concatenation utility class.
 * This class relies on a StringBuilder object.
 * However if the only concatenated characters comes from a single string object then this class toString() will avoid a double character array copy.
 * @author rbossy
 *
 */
public class StringCat implements CharSequence {
	private StringBuilder sb;
	private CharSequence cs;

	/**
	 * Creates a new string concatenation.
	 */
	public StringCat() {
		super();
	}

	@Override
	public char charAt(int index) {
		if (cs == null)
			throw new IndexOutOfBoundsException();
		return cs.charAt(index);
	}

	@Override
	public int length() {
		if (cs == null)
			return 0;
		return cs.length();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		if (cs == null)
			return "";
		return cs.subSequence(start, end);
	}

	@Override
	public boolean equals(Object other) {
		if (cs == null)
			return other == null;
		return cs.equals(other);
	}

	@Override
	public int hashCode() {
		if (cs == null)
			return 0;
		return cs.hashCode();
	}

	@Override
	public String toString() {
		if (cs == null)
			return "";
		return cs.toString();
	}

	/**
	 * Appends characters.
	 * @param a
	 */
	public void append(CharSequence a) {
		if (a == null)
			return;
		if (a.length() == 0)
			return;
		if (cs == null) {
			cs = a;
			return;
		}
		if (sb == null) {
			sb = new StringBuilder(cs.length() + a.length());
			sb.append(cs);
			cs = sb;
		}
		sb.append(a);
	}

	public void append(Object obj) {
		if (obj == null)
			return;
		if (obj instanceof CharSequence)
			append((CharSequence) obj);
		else {
			if (sb == null) {
				sb = new StringBuilder();
				if (cs != null)
					sb.append(cs);
				cs = sb;
			}
			sb.append(obj);
		}
	}
	
	/**
	 * Resets this string cache.
	 */
	public void clear() {
		sb = null;
		cs = null;
	}
	
	/**
	 * Returns true iff this string cache is empty, nothing has been appended.
	 */
	public boolean isEmpty() {
		if (sb != null)
			return sb.length() == 0;
		if (cs != null)
			return cs.length() == 0;
		return true;
	}
}
