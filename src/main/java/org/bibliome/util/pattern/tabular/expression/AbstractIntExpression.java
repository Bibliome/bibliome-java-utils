package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.pattern.tabular.TabularContext;
import org.bibliome.util.pattern.tabular.TabularExpression;

public abstract class AbstractIntExpression extends TabularExpression {
	protected AbstractIntExpression() {
		super();
	}

	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		return TabularContext.intToBoolean(getInt(context, columns));
	}

	@Override
	public String getString(TabularContext context, List<String> columns) {
		return TabularContext.intToString(getInt(context, columns));
	}
}
