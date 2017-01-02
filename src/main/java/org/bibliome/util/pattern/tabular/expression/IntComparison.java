package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.NumericComparisonOperator;
import org.bibliome.util.pattern.tabular.TabularContext;
import org.bibliome.util.pattern.tabular.TabularExpression;

public class IntComparison extends AbstractBooleanExpression {
	private final NumericComparisonOperator operator;
	private final TabularExpression left;
	private final TabularExpression right;
	
	public IntComparison(NumericComparisonOperator operator, TabularExpression left, TabularExpression right) {
		super();
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	public static boolean any(List<String> columns, NumericComparisonOperator operator, int right) {
		for (String col : columns) {
			final int left = TabularContext.stringToInt(col);
			if (operator.compare(left, right)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		if (left == null) {
			final int right = this.right.getInt(context, columns);
			return any(columns, operator, right);
		}
		final int left = this.left.getInt(context, columns);
		final int right = this.right.getInt(context, columns);
		return operator.compare(left, right);
	}
}
