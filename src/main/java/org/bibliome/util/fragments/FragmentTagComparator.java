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

package org.bibliome.util.fragments;

import java.util.Comparator;

public class FragmentTagComparator<F extends Fragment> implements Comparator<FragmentTag<F>> {
	private final Comparator<F> fragmentComparator;
	
	public FragmentTagComparator(Comparator<F> fragmentComparator) {
		super();
		this.fragmentComparator = fragmentComparator;
	}
	
	public FragmentTagComparator() {
		this(new StableComparator<F>());
	}

	@Override
	public int compare(FragmentTag<F> a, FragmentTag<F> b) {
		int aPosition = a.getPosition();
		int bPosition = b.getPosition();
		if (aPosition != bPosition)
			return Integer.compare(aPosition, bPosition);
		FragmentTagType aType = a.getTagType();
		FragmentTagType bType = b.getTagType();
		if (aType != bType)
			return aType.compareTo(bType);
		int aLen = a.getFragmentLength();
		int bLen = b.getFragmentLength();
		if (aLen != bLen) {
			return aType.compareFragments(Integer.compare(bLen, aLen));
		}
		return aType.compareFragments(fragmentComparator.compare(a.getFragment(), b.getFragment()));
	}
}
