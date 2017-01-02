package org.bibliome.util.pattern;

import java.util.List;

import org.bibliome.util.filters.ParamFilter;
import org.bibliome.util.mappers.Mapper;

/**
 * Capturing group.
 * @author rbossy
 *
 * @param <T>
 */
public class CapturingGroup<T,P,F extends ParamFilter<T,P>> extends Group<T,P,F> {
	private final String name;

	/**
	 * Creates a capturing group with the specified quantifier and name.
	 * @param quantifier
	 * @param name
	 */
	public CapturingGroup(Quantifier quantifier, String name) {
		super(quantifier, "(");
		this.name = name;
	}

	/**
	 * Returns this capturing group name.
	 */
	public String getName() {
		return name;
	}

	@Override
	void collectCapturingGroups(List<CapturingGroup<T,P,F>> capturingGroups) {
		capturingGroups.add(this);
		super.collectCapturingGroups(capturingGroups);
	}

	@Override
	public <T2,P2,F2 extends ParamFilter<T2,P2>> CapturingGroup<T2,P2,F2> copy(Mapper<F,F2> mapper) {
		CapturingGroup<T2,P2,F2> result = new CapturingGroup<T2,P2,F2>(getQuantifier(), name);
		copyChildren(result, mapper);
		return result;
	}

	@Override
	public <Q,R> R accept(ClauseVisitor<Q,R,T,P,F> visitor, Q param) {
		return visitor.visit(this, param);
	}
}
