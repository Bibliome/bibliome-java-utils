package org.bibliome.util.fragments;

public abstract class StringBuilderFragmentTagIterator<F extends Fragment> implements FragmentTagIterator<String,F> {
	protected final StringBuilder result = new StringBuilder();
	
	@Override
	public void handleGap(String param, int from, int to) {
		result.append(escape(param.substring(from, to)));
	}

	@Override
	public void handleHead(String param, int to) {
		result.append(escape(param.substring(0, to)));
	}

	@Override
	public void handleTail(String param, int from) {
		result.append(escape(param.substring(from)));
	}
	
	protected abstract String escape(String s);
	
	public String getResult() {
		return result.toString();
	}
}
