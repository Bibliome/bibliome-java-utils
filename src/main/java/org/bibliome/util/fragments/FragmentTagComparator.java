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
