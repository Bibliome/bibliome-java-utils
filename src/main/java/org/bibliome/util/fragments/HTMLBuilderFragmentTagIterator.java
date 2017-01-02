package org.bibliome.util.fragments;

import java.util.Map;

import org.bibliome.util.Strings;

public abstract class HTMLBuilderFragmentTagIterator<F extends Fragment> extends StringBuilderFragmentTagIterator<F> {
	@Override
	public void handleTag(String param, FragmentTag<F> tag) {
		F fragment = tag.getFragment();
		String tagName = getTagName(fragment);
		switch (tag.getTagType()) {
			case CLOSE:
				result.append("</");
				result.append(tagName);
				result.append('>');
				break;
			case OPEN:
				result.append('<');
				appendAttributes(fragment);
				result.append('>');
				break;
			case EMPTY:
				result.append('<');
				appendAttributes(fragment);
				result.append("/>");
				break;
		}
	}
	
	private void appendAttributes(F fragment) {
		for (Map.Entry<String,String> e : getAttributes(fragment).entrySet()) {
			result.append(' ');
			result.append(e.getKey());
			result.append("=\"");
			result.append(escape(e.getValue()));
			result.append('\"');
		}		
	}
	
	protected abstract String getTagName(F fragment);
	
	protected abstract Map<String,String> getAttributes(F fragment);

	@Override
	protected String escape(String s) {
		return Strings.escapeXML(s);
	}
}
