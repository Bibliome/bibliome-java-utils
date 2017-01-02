package org.bibliome.util.pattern;

/**
 * Quantifier types.
 * @author rbossy
 *
 */
public enum QuantifierType {
	GREEDY(""),
	RELUCTANT("?"),
	POSSESSIVE("+");
	
	/**
	 * Regular expression modifier.
	 */
	public final String operatorModifier;

	private QuantifierType(String operatorModifier) {
		this.operatorModifier = operatorModifier;
	}
}
