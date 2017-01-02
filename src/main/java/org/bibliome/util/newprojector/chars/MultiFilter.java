package org.bibliome.util.newprojector.chars;

import java.util.ArrayList;
import java.util.List;

import org.bibliome.util.newprojector.CharFilter;

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
