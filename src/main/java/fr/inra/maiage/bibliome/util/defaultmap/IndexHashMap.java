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

package fr.inra.maiage.bibliome.util.defaultmap;

import java.util.LinkedHashMap;

/**
 * An index associates each element to an integer value unique within the set. 
 * @author rbossy
 */
public class IndexHashMap<T> extends DefaultMap<T,Integer> {
	public IndexHashMap() {
		super(true, new LinkedHashMap<T,Integer>());
	}

	@Override
	protected Integer defaultValue(T key) {
		return size();
	}
}
