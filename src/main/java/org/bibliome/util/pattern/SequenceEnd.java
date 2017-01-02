package org.bibliome.util.pattern;

import java.util.Collection;
import java.util.List;

import org.bibliome.util.filters.ParamFilter;
import org.bibliome.util.mappers.Mapper;

/**
 * Matches the sequence end.
 * @author rbossy
 *
 * @param <T>
 */
public class SequenceEnd<T,P,F extends ParamFilter<T,P>> extends Clause<T,P,F> {
	/**
	 * Creates a sequence end clause.
	 */
	public SequenceEnd() {
		super();
	}

	@Override
	void collectFilters(Collection<F> filters) {
	}

	@Override
	void toRegexp(StringBuilder sb, List<F> filters) {
		sb.append('$');
	}

	@Override
	void collectCapturingGroups(List<CapturingGroup<T,P,F>> capturingGroups) {
	}

	@Override
	public <T2,P2,F2 extends ParamFilter<T2,P2>> SequenceEnd<T2,P2,F2> copy(Mapper<F,F2> mapper) {
		return new SequenceEnd<T2,P2,F2>();
	}

	@Override
	public <Q,R> R accept(ClauseVisitor<Q,R,T,P,F> visitor, Q param) {
		return visitor.visit(this, param);
	}
}
