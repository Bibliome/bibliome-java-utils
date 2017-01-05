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

package org.bibliome.util;

/**
 * Comparison operators of double values.
 * Intended for expression interpreters.
 * @author rbossy
 *
 */
public enum NumericComparisonOperator {
	EQ("==") {
		@Override
		public boolean compare(double a, double b) {
			return a == b;
		}

		@Override
		public boolean compare(int a, int b) {
			return a == b;
		}
	},

	NE("!=") {
		@Override
		public boolean compare(double a, double b) {
			return a != b;
		}

		@Override
		public boolean compare(int a, int b) {
			return a != b;
		}
	},
	
	LE("<=") {
		@Override
		public boolean compare(double a, double b) {
			return a <= b;
		}

		@Override
		public boolean compare(int a, int b) {
			return a <= b;
		}
	},

	GE(">=") {
		@Override
		public boolean compare(double a, double b) {
			return a >= b;
		}

		@Override
		public boolean compare(int a, int b) {
			return a >= b;
		}
	},
	
	LT("<") {
		@Override
		public boolean compare(double a, double b) {
			return a < b;
		}

		@Override
		public boolean compare(int a, int b) {
			return a < b;
		}
	},
	
	GT(">") {
		@Override
		public boolean compare(double a, double b) {
			return a > b;
		}

		@Override
		public boolean compare(int a, int b) {
			return a > b;
		}
	};

	/**
	 * Operator.
	 */
	public final String operator;
	
	private NumericComparisonOperator(String operator) {
		this.operator = operator;
	}
	
	public static NumericComparisonOperator get(String operator) {
		for (NumericComparisonOperator op : NumericComparisonOperator.values())
			if (op.operator.equals(operator))
				return op;
		return null;
	}

	/**
	 * Compare the specified operands.
	 * @param a
	 * @param b
	 */
	public abstract boolean compare(double a, double b);
	
	/**
	 * Compare the specified operands.
	 * @param a
	 * @param b
	 */
	public abstract boolean compare(int a, int b);
}
