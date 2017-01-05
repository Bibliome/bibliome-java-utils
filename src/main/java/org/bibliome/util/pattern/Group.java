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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bibliome.util.filters.ParamFilter;
import org.bibliome.util.mappers.Mapper;

/**
 * Clause group.
 * @author rbossy
 *
 * @param <T>
 */
public class Group<T,P,F extends ParamFilter<T,P>> extends QuantifiedClause<T,P,F> {
	private final List<Clause<T,P,F>> children = new ArrayList<Clause<T,P,F>>();
	private final String opening;
	
	/**
	 * Creates a new group with the specified quantifier and opening parenthesis.
	 * @param quantifier
	 * @param opening
	 */
	Group(Quantifier quantifier, String opening) {
		super(quantifier);
		this.opening = opening;
	}

	/**
	 * Creates a non-capturing group with the specified quantifier and "(?:" as opening parenthesis.
	 * @param quantifier
	 */
	public Group(Quantifier quantifier) {
		this(quantifier, "(?:");
	}
	
	/**
	 * Returns all children clauses.
	 */
	public List<Clause<T,P,F>> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Adds a child quantified clause to this group.
	 * @param clause
	 */
	public void addChild(Clause<T,P,F> clause) {
		children.add(clause);
	}

	/**
	 * Adds a child predicate with the specified filter and the default quantifier to this clause.
	 * @param filter
	 */
	public void addChild(F filter) {
		addChild(new Predicate<T,P,F>(Quantifier.DEFAULT, filter));
	}

	/**
	 * Adds the specified clauses to this group.
	 * @param children
	 */
	public void addChildren(Collection<Clause<T,P,F>> children) {
		this.children.addAll(children);
	}

	@Override
	public void collectFilters(Collection<F> filters) {
		for (Clause<T,P,F> clause : children)
			clause.collectFilters(filters);
	}
	
	@Override
	protected void _toRegexp(StringBuilder sb, List<F> filters) {
		sb.append(opening);
		for (Clause<T,P,F> clause : children)
			clause.toRegexp(sb, filters);
		sb.append(")");
	}

	@Override
	void collectCapturingGroups(List<CapturingGroup<T,P,F>> capturingGroups) {
		for (Clause<T,P,F> clause : children)
			clause.collectCapturingGroups(capturingGroups);
	}

	@Override
	public <T2,P2,F2 extends ParamFilter<T2,P2>> Group<T2,P2,F2> copy(Mapper<F,F2> mapper) {
		Group<T2,P2,F2> result = new Group<T2,P2,F2>(getQuantifier(), opening);
		copyChildren(result, mapper);
		return result;
	}
	
	protected <T2,P2,F2 extends ParamFilter<T2,P2>> void copyChildren(Group<T2,P2,F2> group, Mapper<F,F2> mapper) {
		for (Clause<T,P,F> c : children)
			group.addChild(c.copy(mapper));
	}

	@Override
	public <Q,R> R accept(ClauseVisitor<Q,R,T,P,F> visitor, Q param) {
		return visitor.visit(this, param);
	}
}
