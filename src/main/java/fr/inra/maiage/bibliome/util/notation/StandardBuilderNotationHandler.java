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

package fr.inra.maiage.bibliome.util.notation;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StandardBuilderNotationHandler extends AbstractListBuilderNotationHandler<List<Object>> {
	public StandardBuilderNotationHandler() {
		super();
	}

	@Override
	protected List<Object> createList() {
		return new ArrayList<Object>();
	}

	@Override
	protected List<Object> createList(List<Object> parent) {
		return new ArrayList<Object>();
	}

	@Override
	protected void addList(List<Object> list, List<Object> value) {
		list.add(value);
	}

	@Override
	protected void addMap(List<Object> list, String key, List<Object> value) {
		Map.Entry<String,List<Object>> map = new AbstractMap.SimpleImmutableEntry<String,List<Object>>(key, value);
		list.add(map);
	}

	@Override
	protected void addString(List<Object> list, String value) {
		list.add(value);
	}
}
