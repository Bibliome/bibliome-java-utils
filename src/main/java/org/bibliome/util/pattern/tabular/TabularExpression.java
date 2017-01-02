package org.bibliome.util.pattern.tabular;

import java.util.List;

import org.bibliome.util.filters.ParamFilter;

public abstract class TabularExpression implements ParamFilter<List<String>,TabularContext> {
	protected TabularExpression() {
		super();
	}
	
	public abstract boolean getBoolean(TabularContext context, List<String> columns);
	public abstract int getInt(TabularContext context, List<String> columns);
	public abstract String getString(TabularContext context, List<String> columns);
	
	@Override
	public boolean accept(List<String> x, TabularContext context) {
		return getBoolean(context, x);
	}
}
