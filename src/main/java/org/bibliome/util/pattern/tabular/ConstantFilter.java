package org.bibliome.util.pattern.tabular;

import java.util.List;
import java.util.regex.Pattern;

public interface ConstantFilter {
	boolean acceptConstantString(List<String> columns, String value);
	boolean acceptConstantInt(List<String> columns, int value);
	boolean acceptRegex(List<String> columns, Pattern pattern);
}
