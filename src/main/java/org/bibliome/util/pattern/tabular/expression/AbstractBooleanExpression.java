package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.pattern.tabular.TabularContext;
import org.bibliome.util.pattern.tabular.TabularExpression;

public abstract class AbstractBooleanExpression extends TabularExpression {
	protected AbstractBooleanExpression() {
		super();
	}

	@Override
	public int getInt(TabularContext context, List<String> columns) {
		return TabularContext.booleanToInt(getBoolean(context, columns));
	}

	@Override
	public String getString(TabularContext context, List<String> columns) {
		return TabularContext.booleanToString(getBoolean(context, columns));
	}
}
