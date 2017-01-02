package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.pattern.tabular.TabularContext;

public class IntReference extends AbstractStringExpression {
	private final int column;

	public IntReference(int column) {
		super();
		this.column = column;
	}

	@Override
	public String getString(TabularContext context, List<String> columns) {
		return TabularContext.getColumnValue(columns, column);
	}
}
