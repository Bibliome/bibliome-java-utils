package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.pattern.tabular.TabularContext;
import org.bibliome.util.pattern.tabular.TabularExpression;

public class Not extends AbstractBooleanExpression {
	private final TabularExpression expr;

	public Not(TabularExpression expr) {
		super();
		this.expr = expr;
	}

	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		return !expr.getBoolean(context, columns);
	}
}
