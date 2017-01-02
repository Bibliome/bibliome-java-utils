package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.pattern.tabular.TabularContext;
import org.bibliome.util.pattern.tabular.TabularExpression;

public abstract class AbstractStringExpression extends TabularExpression {
	protected AbstractStringExpression() {
		super();
	}

	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		return TabularContext.stringToBoolean(getString(context, columns));
	}

	@Override
	public int getInt(TabularContext context, List<String> columns) {
		return TabularContext.stringToInt(getString(context, columns));
	}
}
