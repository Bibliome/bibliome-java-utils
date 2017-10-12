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

import java.util.List;

import fr.inra.maiage.bibliome.util.filters.ParamFilter;

/**
 * Base class of clauses that may be quantified.
 * @author rbossy
 *
 * @param <T>
 */
public abstract class QuantifiedClause<T,P,F extends ParamFilter<T,P>> extends Clause<T,P,F> {
	private final Quantifier quantifier;

	/**
	 * Creates a quantified clause with the specified quantifier.
	 * @param quantifier
	 */
	protected QuantifiedClause(Quantifier quantifier) {
		super();
		this.quantifier = quantifier;
	}

	/**
	 * Returns this clause quantifier.
	 */
	public Quantifier getQuantifier() {
		return quantifier;
	}

	@Override
	final void toRegexp(StringBuilder sb, List<F> filters) {
		_toRegexp(sb, filters);
		sb.append(quantifier.toString());
	}

	/**
	 * Generates the regular expression corrsponding to this clause.
	 * @param sb
	 * @param filters
	 */
	protected abstract void _toRegexp(StringBuilder sb, List<F> filters);
}
