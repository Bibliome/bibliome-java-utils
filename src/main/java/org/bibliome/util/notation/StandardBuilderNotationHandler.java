package org.bibliome.util.notation;

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
