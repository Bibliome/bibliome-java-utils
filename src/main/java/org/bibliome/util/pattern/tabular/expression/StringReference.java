package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.pattern.tabular.TabularContext;
import org.bibliome.util.pattern.tabular.TabularExpression;

public class StringReference extends TabularExpression {
	private final String name;

	public StringReference(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getString(TabularContext context, List<String> columns) {
		return context.getStringValue(context, columns, name);
	}

	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		return context.getBooleanValue(context, columns, name);
	}

	@Override
	public int getInt(TabularContext context, List<String> columns) {
		return context.getIntValue(context, columns, name);
	}
}
