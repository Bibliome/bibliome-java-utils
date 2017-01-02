package org.bibliome.util.taxonomy.reject;

import java.util.regex.Pattern;

import org.bibliome.util.taxonomy.Name;

/**
 * Reject names that matches a regular expression.
 * @author rbossy
 *
 */
public class RejectNamePattern implements RejectName {
	private final Pattern pattern;

	/**
	 * Creates a name reject based on the specified pattern.
	 * @param pattern
	 */
	public RejectNamePattern(Pattern pattern) {
		super();
		this.pattern = pattern;
	}

	@Override
	public boolean reject(int taxid, Name name) {
		return pattern.matcher(name.name).matches();
	}
}
