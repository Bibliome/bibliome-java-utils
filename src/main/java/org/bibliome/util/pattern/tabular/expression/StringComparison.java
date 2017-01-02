package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.StringComparisonOperator;
import org.bibliome.util.pattern.tabular.TabularContext;
import org.bibliome.util.pattern.tabular.TabularExpression;

public class StringComparison extends AbstractBooleanExpression {
	private final StringComparisonOperator operator;
	private final TabularExpression left;
	private final TabularExpression right;
	
	public StringComparison(StringComparisonOperator operator, TabularExpression left, TabularExpression right) {
		super();
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	public static boolean any(List<String> columns, StringComparisonOperator operator, String right) {
		for (String left : columns) {
			if (operator.accept(left, right)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		if (left == null) {
			String right = this.right.getString(context, columns);
			return any(columns, operator, right);
		}
		String left = this.left.getString(context, columns);
		String right = this.right.getString(context, columns);
		return operator.accept(left, right);
	}
}
