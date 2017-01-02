package org.bibliome.util.pattern;

import java.util.Collection;
import java.util.List;

import org.bibliome.util.filters.ParamFilter;
import org.bibliome.util.mappers.Mapper;

/**
 * Clause that accepts object according to a filter.
 * @author rbossy
 *
 * @param <T>
 */
public class Predicate<T,P,F extends ParamFilter<T,P>> extends QuantifiedClause<T,P,F> {
	private final F filter;

	/**
	 * Creates a predicate with the specified quantifier
	 * @param quantifier
	 * @param filter
	 */
	public Predicate(Quantifier quantifier, F filter) {
		super(quantifier);
		this.filter = filter;
	}

	/**
	 * Returns this predicate filter.
	 */
	public F getFilter() {
		return filter;
	}

	@Override
	public void collectFilters(Collection<F> filters) {
		filters.add(filter);
	}

	@Override
	protected void _toRegexp(StringBuilder sb, List<F> filters) {
		int f = filters.indexOf(filter);
		int n = filters.size();
		if (getQuantifier() != Quantifier.DEFAULT)
			sb.append("(?:");
		sb.append(SequenceChars.TOKEN);
		for (int i = 0; i < n; ++i)
			sb.append(i == f ? SequenceChars.TRUE : '.');
		if (getQuantifier() != Quantifier.DEFAULT)
			sb.append(')');
	}

	@Override
	void collectCapturingGroups(List<CapturingGroup<T,P,F>> capturingGroups) {
	}

	@Override
	public <T2,P2,F2 extends ParamFilter<T2,P2>> Predicate<T2,P2,F2> copy(Mapper<F,F2> mapper) {
		return new Predicate<T2,P2,F2>(getQuantifier(), mapper.map(filter));
	}

	@Override
	public <Q,R> R accept(ClauseVisitor<Q,R,T,P,F> visitor, Q param) {
		return visitor.visit(this, param);
	}
}
