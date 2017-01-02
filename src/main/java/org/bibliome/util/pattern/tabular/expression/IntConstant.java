package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.pattern.tabular.ConstantFilter;
import org.bibliome.util.pattern.tabular.TabularContext;

public class IntConstant extends AbstractIntExpression {
	private final int value;

	public IntConstant(int value) {
		super();
		this.value = value;
	}

	@Override
	public int getInt(TabularContext context, List<String> columns) {
		return value;
	}

	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		ConstantFilter constantFilter = context.getConstantFilter();
		return constantFilter.acceptConstantInt(columns, value);
	}
}
