package org.bibliome.util.pattern.tabular.expression;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.pattern.tabular.TabularContext;
import org.bibliome.util.pattern.tabular.TabularExpression;

public class MatchRegexp extends TabularExpression {
	private final TabularExpression target;
	private final Pattern pattern;
	
	public MatchRegexp(TabularExpression target, Pattern pattern) {
		super();
		this.target = target;
		this.pattern = pattern;
	}
	
	private Matcher getMatcher(TabularContext context, List<String> columns) {
		String target = this.target.getString(context, columns);
		return pattern.matcher(target);
	}
	
	public static boolean any(List<String> columns, Pattern pattern) {
		for (String col : columns) {
			Matcher m = pattern.matcher(col);
			if (m.find()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		if (target == null) {
			return any(columns, pattern);
		}
		Matcher m = getMatcher(context, columns);
		return m.find();
	}

	@Override
	public int getInt(TabularContext context, List<String> columns) {
		if (target == null) {
			int result = 0;
			for (String col : columns) {
				Matcher m = pattern.matcher(col);
				int n = 0;
				while (m.find()) {
					n++;
				}
				result = Math.max(result, n);
			}
			return result;
		}
		Matcher m = getMatcher(context, columns);
		int result = 0;
		while (m.find()) {
			result++;
		}
		return result;
	}

	@Override
	public String getString(TabularContext context, List<String> columns) {
		if (target == null) {
			for (String col : columns) {
				Matcher m = pattern.matcher(col);
				if (m.find()) {
					return m.group();
				}
			}
			return "";
		}
		Matcher m = getMatcher(context, columns);
		if (m.find()) {
			return m.group();
		}
		return "";
	}
}
