package org.bibliome.util.pattern;

import java.util.List;

import org.bibliome.util.filters.ParamFilter;

/**
 * Base class of clauses that may be quantified.
 * @author rbossy
 *
 * @param <T>
 */
public abstract class QuantifiedClause<T,P,F extends ParamFilter<T,P>> extends Clause<T,P,F> {
	private final Quantifier quantifier;

	/**
	 * Creates a quantified clause with the specified quantifier.
	 * @param quantifier
	 */
	protected QuantifiedClause(Quantifier quantifier) {
		super();
		this.quantifier = quantifier;
	}

	/**
	 * Returns this clause quantifier.
	 */
	public Quantifier getQuantifier() {
		return quantifier;
	}

	@Override
	final void toRegexp(StringBuilder sb, List<F> filters) {
		_toRegexp(sb, filters);
		sb.append(quantifier.toString());
	}

	/**
	 * Generates the regular expression corrsponding to this clause.
	 * @param sb
	 * @param filters
	 */
	protected abstract void _toRegexp(StringBuilder sb, List<F> filters);
}
