package org.bibliome.util.pattern.tabular.expression;

import java.util.List;
import java.util.regex.Pattern;

import org.bibliome.util.pattern.tabular.TabularContext;

public class DefaultMatchRegexp extends AbstractBooleanExpression {
	private final Pattern pattern;

	public DefaultMatchRegexp(Pattern pattern) {
		super();
		this.pattern = pattern;
	}

	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		return context.getConstantFilter().acceptRegex(columns, pattern);
	}
}
