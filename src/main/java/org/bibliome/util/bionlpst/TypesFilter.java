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

package org.bibliome.util.bionlpst;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.bibliome.util.filters.Filter;

public class TypesFilter implements Filter<BioNLPSTAnnotation> {
	private final Collection<String> types = new HashSet<String>();

	public TypesFilter(String... types) {
		super();
		this.types.addAll(Arrays.asList(types));
	}
	
	public void addType(String type) {
		types.add(type);
	}

	@Override
	public boolean accept(BioNLPSTAnnotation x) {
		return types.contains(x.getType());
	}
}
