/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package fr.inra.maiage.bibliome.util.pattern.tabular;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.util.NumericComparisonOperator;
import fr.inra.maiage.bibliome.util.StringComparisonOperator;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultMap;
import fr.inra.maiage.bibliome.util.pattern.tabular.expression.AbstractIntExpression;
import fr.inra.maiage.bibliome.util.pattern.tabular.expression.IntComparison;
import fr.inra.maiage.bibliome.util.pattern.tabular.expression.IntReference;
import fr.inra.maiage.bibliome.util.pattern.tabular.expression.MatchRegexp;
import fr.inra.maiage.bibliome.util.pattern.tabular.expression.StringComparison;
import fr.inra.maiage.bibliome.util.pattern.tabular.expression.StringConstant;

public class TabularContext {
	private static class ValueNames extends DefaultMap<String,TabularExpression> {
		private ValueNames() {
			super(false, new HashMap<String,TabularExpression>());
		}

		@Override
		protected TabularExpression defaultValue(String key) {
			return StringConstant.EMPTY;
		}
	}
	
	private static final TabularExpression LINE_LENGTH = new AbstractIntExpression() {
		@Override
		public int getInt(TabularContext context, List<String> columns) {
			return columns.size();
		}
	};
	
	public final ConstantFilter ANY = new ConstantFilter() {
		@Override
		public boolean acceptConstantString(List<String> columns, String value) {
			return StringComparison.any(columns, StringComparisonOperator.EQ, value);
		}

		@Override
		public boolean acceptConstantInt(List<String> columns, int value) {
			return IntComparison.any(columns, NumericComparisonOperator.EQ, value);
		}

		@Override
		public boolean acceptRegex(List<String> columns, Pattern pattern) {
			return MatchRegexp.any(columns, pattern);
		}
	};
	
	public class DefaultColumn implements ConstantFilter {
		private final int column;

		public DefaultColumn(int column) {
			super();
			this.column = column;
		}

		@Override
		public boolean acceptConstantString(List<String> columns, String value) {
			String col = getColumnValue(columns, column);
			return StringComparisonOperator.EQ.accept(value, col);
		}

		@Override
		public boolean acceptConstantInt(List<String> columns, int value) {
			String sCol = getColumnValue(columns, column);
			int iCol = stringToInt(sCol);
			return NumericComparisonOperator.EQ.compare(value, iCol);
		}

		@Override
		public boolean acceptRegex(List<String> columns, Pattern pattern) {
			String col = getColumnValue(columns, column);
			Matcher m = pattern.matcher(col);
			return m.find();
		}
	}

	private final ValueNames valueNames = new ValueNames();
	private ConstantFilter constantFilter = ANY;

	public TabularContext() {
		super();
		setValueName("#", LINE_LENGTH);
	}

	private static boolean isValidColumn(List<String> currentColumns, int columnNumber) {
		return (currentColumns != null) && (columnNumber >= 0) && (columnNumber < currentColumns.size());
	}

	public static String getColumnValue(List<String> currentColumns, int columnNumber) {
		if (isValidColumn(currentColumns, columnNumber)) {
			return currentColumns.get(columnNumber);
		}
		return "";
	}
	
	public boolean getBooleanValue(TabularContext context, List<String> columns, String name) {
		return valueNames.safeGet(name).getBoolean(context, columns);
	}
	
	public int getIntValue(TabularContext context, List<String> columns, String name) {
		return valueNames.safeGet(name).getInt(context, columns);
	}
	
	public String getStringValue(TabularContext context, List<String> columns, String name) {
		return valueNames.safeGet(name).getString(context, columns);
	}
	
	public static boolean intToBoolean(int i) {
		return i != 0;
	}
	
	public static boolean stringToBoolean(String s) {
		return s != null && !s.isEmpty();
	}
	
	public static int booleanToInt(boolean b) {
		return b ? 1 : 0;
	}
	
	public static int stringToInt(String s) {
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static String booleanToString(boolean b) {
		return b ? "yes" : "";
	}
	
	public static String intToString(int i) {
		return Integer.toString(i);
	}
	
	public void setValueName(String name, TabularExpression expr) {
		valueNames.put(name, expr);
	}
	
	public void setColumnName(String name, int n) {
		valueNames.put(name, new IntReference(n));
	}

	public ConstantFilter getConstantFilter() {
		return constantFilter;
	}

	void setDefaultColumn(int column) {
		this.constantFilter = new DefaultColumn(column);
	}
}
