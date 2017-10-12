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

package fr.inra.maiage.bibliome.util.pattern.tabular;

import java.util.List;

import fr.inra.maiage.bibliome.util.filters.ParamFilter;

public abstract class TabularExpression implements ParamFilter<List<String>,TabularContext> {
	protected TabularExpression() {
		super();
	}
	
	public abstract boolean getBoolean(TabularContext context, List<String> columns);
	public abstract int getInt(TabularContext context, List<String> columns);
	public abstract String getString(TabularContext context, List<String> columns);
	
	@Override
	public boolean accept(List<String> x, TabularContext context) {
		return getBoolean(context, x);
	}
}
