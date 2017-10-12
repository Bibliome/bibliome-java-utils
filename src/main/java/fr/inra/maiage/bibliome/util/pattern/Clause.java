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

package fr.inra.maiage.bibliome.util.pattern;

import java.util.Collection;
import java.util.List;

import fr.inra.maiage.bibliome.util.filters.ParamFilter;
import fr.inra.maiage.bibliome.util.mappers.Mapper;

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
