package org.bibliome.util.fragments;

public interface FragmentTagIterator<P,F extends Fragment> {
	void handleTag(P param, FragmentTag<F> tag);
	void handleGap(P param, int from, int to);
	void handleHead(P param, int to);
	void handleTail(P param, int from);
}
