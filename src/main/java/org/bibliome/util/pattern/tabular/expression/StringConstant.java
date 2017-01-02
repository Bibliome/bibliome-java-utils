package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.pattern.tabular.ConstantFilter;
import org.bibliome.util.pattern.tabular.TabularContext;

public class StringConstant extends AbstractStringExpression {
	private final String value;

	public StringConstant(String value) {
		super();
		this.value = value;
	}

	@Override
	public String getString(TabularContext context, List<String> columns) {
		return value;
	}
	
	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		ConstantFilter constantFilter = context.getConstantFilter();
		return constantFilter.acceptConstantString(columns, value);
	}

	public static final StringConstant EMPTY = new StringConstant("");
}
