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

package fr.inra.maiage.bibliome.util.newprojector.chars;

import java.util.ArrayList;
import java.util.List;

import fr.inra.maiage.bibliome.util.newprojector.CharFilter;

class MultiFilter implements CharFilter {
	private final List<CharFilter> charFilters = new ArrayList<CharFilter>(2);
	
	MultiFilter(CharFilter cf1, CharFilter cf2) {
		super();
		charFilters.add(cf1);
		charFilters.add(cf2);
	}


	@Override
	public boolean accept(int last, char c) {
		for (CharFilter cf : charFilters)
			if (!cf.accept(last, c))
				return false;
		return true;
	}

	@Override
	public CharFilter combine(CharFilter cf) {
		charFilters.add(cf);
		return this;
	}
}
