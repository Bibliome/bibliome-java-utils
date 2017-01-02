package org.bibliome.util.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bibliome.util.filters.ParamFilter;
import org.bibliome.util.mappers.Mapper;

/**
 * Union of several clauses.
 * @author rbossy
 *
 * @param <T>
 */
public class Alternatives<T,P,F extends ParamFilter<T,P>> extends Clause<T,P,F> {
	private final List<Clause<T,P,F>> alternatives = new ArrayList<Clause<T,P,F>>();

	/**
	 * Creates an union.
	 */
	public Alternatives() {
		super();
	}
	
	public List<Clause<T,P,F>> getAlternatives() {
		return Collections.unmodifiableList(alternatives);
	}

	/**
	 * Adds an alternative to this union.
	 * @param alt
	 */
	public void add(Clause<T,P,F> alt) {
		alternatives.add(alt);
	}

	@Override
	void collectCapturingGroups(List<CapturingGroup<T,P,F>> capturingGroups) {
		for (Clause<T,P,F> clause : alternatives)
			clause.collectCapturingGroups(capturingGroups);
	}

	@Override
	void collectFilters(Collection<F> filters) {
		for (Clause<T,P,F> clause : alternatives)
			clause.collectFilters(filters);
	}

	@Override
	void toRegexp(StringBuilder sb, List<F> filters) {
		boolean notFirst = false;
		for (Clause<T,P,F> clause : alternatives) {
			if (notFirst)
				sb.append('|');
			else
				notFirst = true;
			sb.append("(?:");
			clause.toRegexp(sb, filters);
			sb.append(')');
		}
	}

	@Override
	public <T2,P2,F2 extends ParamFilter<T2,P2>> Alternatives<T2,P2,F2> copy(Mapper<F,F2> mapper) {
		Alternatives<T2,P2,F2> result = new Alternatives<T2,P2,F2>();
		for (Clause<T,P,F> c : alternatives)
			result.add(c.copy(mapper));
		return result;
	}

	@Override
	public <Q,R> R accept(ClauseVisitor<Q,R,T,P,F> visitor, Q param) {
		return visitor.visit(this, param);
	}
}
