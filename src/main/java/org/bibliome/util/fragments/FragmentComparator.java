package org.bibliome.util.fragments;

import java.util.Comparator;

public class FragmentComparator<F extends Fragment> implements Comparator<F> {
	@Override
	public int compare(F a, F b) {
		if (a.getStart() == b.getStart())
			return b.getEnd() - a.getEnd();
		return a.getStart() - b.getStart();
	}
}
