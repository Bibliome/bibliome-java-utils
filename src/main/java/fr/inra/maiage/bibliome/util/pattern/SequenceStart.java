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
 * Matches the sequence start.
 * @author rbossy
 *
 * @param <T>
 */
public class SequenceStart<T,P,F extends ParamFilter<T,P>> extends Clause<T,P,F> {
	/**
	 * Creates a sequence start clause.
	 */
	public SequenceStart() {
		super();
	}

	@Override
	void collectFilters(Collection<F> filters) {
	}

	@Override
	void toRegexp(StringBuilder sb, List<F> filters) {
		sb.append('^');
	}

	@Override
	void collectCapturingGroups(List<CapturingGroup<T,P,F>> capturingGroups) {
	}

	@Override
	public <T2,P2,F2 extends ParamFilter<T2,P2>> SequenceStart<T2,P2,F2> copy(Mapper<F,F2> mapper) {
		return new SequenceStart<T2,P2,F2>();
	}

	@Override
	public <Q,R> R accept(ClauseVisitor<Q,R,T,P,F> visitor, Q param) {
		return visitor.visit(this, param);
	}
}
