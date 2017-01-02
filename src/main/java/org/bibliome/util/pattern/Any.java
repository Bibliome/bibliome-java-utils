package org.bibliome.util.pattern;

import java.util.Collection;
import java.util.List;

import org.bibliome.util.filters.ParamFilter;
import org.bibliome.util.mappers.Mapper;

/**
 * Matches any object.
 * @author rbossy
 *
 * @param <T>
 */
public class Any<T,P,F extends ParamFilter<T,P>> extends QuantifiedClause<T,P,F> {
	/**
	 * Creates a match any object clause with the specified quantifier.
	 * @param quantifier
	 */
	public Any(Quantifier quantifier) {
		super(quantifier);
	}

	@Override
	void collectFilters(Collection<F> filters) {
	}

	@Override
	protected void _toRegexp(StringBuilder sb, List<F> filters) {
		sb.append(SequenceChars.TOKEN);
		int n = filters.size();
		for (int i = 0; i < n; ++i)
			sb.append('.');
	}

	@Override
	void collectCapturingGroups(List<CapturingGroup<T,P,F>> capturingGroups) {
	}

	@Override
	public <T2,P2,F2 extends ParamFilter<T2,P2>> Any<T2,P2,F2> copy(Mapper<F,F2> mapper) {
		return new Any<T2,P2,F2>(getQuantifier());
	}

	@Override
	public <Q,R> R accept(ClauseVisitor<Q,R,T,P,F> visitor, Q param) {
		return visitor.visit(this, param);
	}
}
