package org.bibliome.util.pattern;

import java.util.Collection;
import java.util.List;

import org.bibliome.util.filters.ParamFilter;
import org.bibliome.util.mappers.Mapper;

/**
 * Sequence pattern clause.
 * @author rbossy
 *
 * @param <T>
 */
public abstract class Clause<T,P,F extends ParamFilter<T,P>> {
	/**
	 * Collects filters from this clause and sub-clauses.
	 * @param filters
	 */
	abstract void collectFilters(Collection<F> filters);

	/**
	 * Collects capturing groups from this clause and sub-clauses.
	 * @param filters
	 */
	abstract void collectCapturingGroups(List<CapturingGroup<T,P,F>> capturingGroups);

	/**
	 * Translates this clause as a regular expression.
	 * @param sb
	 * @param filters
	 */
	abstract void toRegexp(StringBuilder sb, List<F> filters);
	
	public abstract <T2,P2,F2 extends ParamFilter<T2,P2>> Clause<T2,P2,F2> copy(Mapper<F,F2> mapper);
	
	public abstract <Q,R> R accept(ClauseVisitor<Q,R,T,P,F> visitor, Q param);
}
