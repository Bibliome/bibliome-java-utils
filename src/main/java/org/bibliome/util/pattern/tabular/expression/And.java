package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.pattern.tabular.TabularContext;
import org.bibliome.util.pattern.tabular.TabularExpression;

public class And extends AbstractBooleanExpression {
	private final TabularExpression left;
	private final TabularExpression right;

	public And(TabularExpression left, TabularExpression right) {
		super();
		this.left = left;
		this.right = right;
	}
	
	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		if (left.getBoolean(context, columns)) {
			return right.getBoolean(context, columns);
		}
		return false;
	}
}
