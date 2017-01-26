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

package org.bibliome.util.pattern;

/**
 * Clause quantifier.
 * @author rbossy
 *
 */
public class Quantifier {
	private final String operator;
	
	/**
	 * minimum number of occurrences.
	 */
	public final int minOccurrences;
	
	/**
	 * Maximum number of occurrences.
	 */
	public final Integer maxOccurrences;
	
	/**
	 * Quantifier greediness.
	 */
	public final QuantifierType type;
	
	private Quantifier(String operator, int minOccurrences, Integer maxOccurrences, QuantifierType type) {
		super();
		this.operator = operator == null ? null : operator + type.operatorModifier;
		this.minOccurrences = minOccurrences;
		this.maxOccurrences = maxOccurrences;
		this.type = type;
	}
	
	/**
	 * Creates a quantifier of the specified type and the specified occurrence boundaries.
	 * @param minOccurrences
	 * @param maxOccurrences
	 * @param type
	 */
	public Quantifier(int minOccurrences, Integer maxOccurrences, QuantifierType type) {
		this(null, minOccurrences, maxOccurrences, type);
	}

	/**
	 * Creates a greedy quantifier with the specified occurrence boundaries.
	 * @param minOccurrences
	 * @param maxOccurrences
	 */
	public Quantifier(int minOccurrences, Integer maxOccurrences) {
		this(minOccurrences, maxOccurrences, QuantifierType.GREEDY);
	}
	
	/**
	 * Creates a greedy quantifier with the specified minimum occurrences.
	 * @param minOccurrences
	 */
	public Quantifier(int minOccurrences) {
		this(minOccurrences, null, QuantifierType.GREEDY);
	}
	
	/**
	 * Creates a quantifier of the specified type and the specified minimum occurrences.
	 * @param minOccurrences
	 * @param type
	 */
	public Quantifier(int minOccurrences, QuantifierType type) {
		this(minOccurrences, null, type);
	}

	private void buildPattern(StringBuilder sb) {
		if (operator != null) {
			sb.append(operator);
			return;
		}
		sb.append('{');
		sb.append(minOccurrences);
		if (maxOccurrences == null)
			sb.append(',');
		else if (maxOccurrences.intValue() != minOccurrences) {
			sb.append(',');
			sb.append(maxOccurrences);
		}
		sb.append('}');
		sb.append(type.operatorModifier);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		buildPattern(sb);
		return sb.toString();
	}

	/**
	 * Default quantifier: one occurrence only.
	 */
	public static final Quantifier DEFAULT = new Quantifier("", 1, 1, QuantifierType.GREEDY);
	
	/**
	 * Optional quantifier: greedy 0-1 occurrences.
	 */
	public static final Quantifier OPTIONAL = new Quantifier("?", 0, 1, QuantifierType.GREEDY);
	
	/**
	 * Kleene star quantifier: greedy 0-n occurrences.
	 */
	public static final Quantifier STAR = new Quantifier("*", 0, null, QuantifierType.GREEDY);
	
	/**
	 * Plus quantifier: greedy 1-n occurrences.
	 */
	public static final Quantifier PLUS = new Quantifier("+", 1, null, QuantifierType.GREEDY);
	
	/**
	 * Reluctant optional quantifier.
	 */
	public static final Quantifier RELUCTANT_OPTIONAL = new Quantifier("??", 0, 1, QuantifierType.RELUCTANT);
	
	/**
	 * Reluctant start quantifier.
	 */
	public static final Quantifier RELUCTANT_STAR = new Quantifier("*?", 0, null, QuantifierType.RELUCTANT);
	
	/**
	 * Reluctant plus quantifier.
	 */
	public static final Quantifier RELUCTANT_PLUS = new Quantifier("+?", 1, null, QuantifierType.RELUCTANT);
}
