package org.bibliome.util.newprojector;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 * A dictionary maintains pair of String,T values.
 * Entry keys may be searched inside a target string.
 * @author rbossy
 *
 * @param <T>
 */
public class Dictionary<T> {
	private final State<T> root;
	private final CharFilter filter;
	private final CharMapper mapper;
	private long nKeys = 0;
	private long nValues = 0;
	private long keyLength = 0;
	
	/**
	 * Creates a new dictionary.
	 * @param root root state
	 * @param filter
	 * @param mapper
	 */
	public Dictionary(State<T> root, CharFilter filter, CharMapper mapper) {
		super();
		this.root = root;
		this.filter = filter;
		this.mapper = mapper;
	}

	/**
	 * Returns the character filter.
	 */
	public CharFilter getFilter() {
		return filter;
	}

	/**
	 * Returns the character mapper.
	 */
	public CharMapper getMapper() {
		return mapper;
	}
	
	/**
	 * Adds an entry.
	 * @param key
	 * @param value
	 */
	public void addEntry(CharSequence key, T value) {
		State<T> state = root;
		int last = -1;
		for (int i = 0; i < key.length(); ++i) {
			char c = key.charAt(i);
			if (!filter.accept(last, c))
				continue;
			state = state.extend(mapper.map(last, c));
			last = c;
		}
		if (!state.hasValue()) {
			keyLength += key.length();
			nKeys++;
		}
		nValues++;
		state.addValue(value, key);
	}
	
	/**
	 * Adds all entries in the specified map.
	 * @param map
	 */
	public void addEntries(Map<? extends CharSequence,? extends T> map) {
		for (Map.Entry<? extends CharSequence,? extends T> e : map.entrySet())
			addEntry(e.getKey(), e.getValue());
	}
	
	Match<T> newMatch() {
		Match<T> result = new Match<T>();
		result.setState(root);
		return result;
	}
	
	/**
	 * Searches for entry keys in the specified reader.
	 * @param matcher
	 * @param r
	 * @throws IOException
	 */
	public void match(Matcher<T> matcher, Reader r) throws IOException {
		for (int i = r.read(); i != -1; i = r.read())
			matcher.matchChar((char) i);
	}
	
	/**
	 * Searches for entry keys in the specified character sequence.
	 * @param matcher
	 * @param s
	 */
	public void match(Matcher<T> matcher, CharSequence s) {
		for (int i = 0; i < s.length(); ++i)
			matcher.matchChar(s.charAt(i));
	}
	
	public long countStates() {
		return root.countStates();
	}
	
	public long countKeys() {
		return nKeys;
	}
	
	public long countValues() {
		return nValues;
	}
	
	public long keyLength() {
		return keyLength;
	}
}
