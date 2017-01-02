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
