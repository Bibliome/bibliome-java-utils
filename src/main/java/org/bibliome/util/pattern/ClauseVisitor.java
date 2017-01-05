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
