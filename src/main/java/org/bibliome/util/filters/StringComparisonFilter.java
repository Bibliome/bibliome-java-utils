package org.bibliome.util.filters;

import org.bibliome.util.StringComparisonOperator;

public class StringComparisonFilter implements Filter<String> {
	private final StringComparisonOperator op;
	private final String operand;

	public StringComparisonFilter(StringComparisonOperator op, String operand) {
		super();
		this.op = op;
		this.operand = operand;
	}

	@Override
	public boolean accept(String x) {
		return op.accept(x, operand);
	}
}
