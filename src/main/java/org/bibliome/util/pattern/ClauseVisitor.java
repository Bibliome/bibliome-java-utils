package org.bibliome.util.pattern;

import org.bibliome.util.filters.ParamFilter;

public interface ClauseVisitor<Q,R,T,P,F extends ParamFilter<T,P>> {
	R visit(Alternatives<T,P,F> alt, Q param);
	R visit(Any<T,P,F> any, Q param);
	R visit(CapturingGroup<T,P,F> grp, Q param);
	R visit(Group<T,P,F> grp, Q param);
	R visit(Predicate<T,P,F> pred, Q param);
	R visit(SequenceStart<T,P,F> start, Q param);
	R visit(SequenceEnd<T,P,F> start, Q param);
}
