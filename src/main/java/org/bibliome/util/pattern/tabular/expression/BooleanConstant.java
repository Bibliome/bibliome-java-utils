package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.pattern.tabular.TabularContext;

public class BooleanConstant extends AbstractBooleanExpression {
	private final boolean value;

	private BooleanConstant(boolean value) {
		super();
		this.value = value;
	}

	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		return value;
	}
	
	public static final BooleanConstant TRUE = new BooleanConstant(true);
	public static final BooleanConstant FALSE = new BooleanConstant(false);
}
