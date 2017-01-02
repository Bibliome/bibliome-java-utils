package org.bibliome.util;

/**
 * Arithmetic operators on double values.
 * Intended for expression interpreters.
 * @author rbossy
 *
 */
public enum ArithmeticOperator implements BinaryNumericOperator {
	PLUS('+') {
		@Override
		public double compute(double a, double b) {
			return a + b;
		}

		@Override
		public int compute(int a, int b) {
			return a + b;
		}

		@Override
		public int neutral() {
			return 0;
		}
	},
	
	MINUS('-') {
		@Override
		public double compute(double a, double b) {
			return a - b;
		}

		@Override
		public int compute(int a, int b) {
			return a - b;
		}

		@Override
		public int neutral() {
			return 0;
		}
	},
	
	MULT('*') {
		@Override
		public double compute(double a, double b) {
			return a * b;
		}

		@Override
		public int compute(int a, int b) {
			return a * b;
		}

		@Override
		public int neutral() {
			return 1;
		}
	},
	
	DIV('/') {
		@Override
		public double compute(double a, double b) {
			return a / b;
		}

		@Override
		public int compute(int a, int b) {
			return a / b;
		}

		@Override
		public int neutral() {
			return 1;
		}
	},
	
	MOD('%') {
		@Override
		public double compute(double a, double b) {
			return a % b;
		}

		@Override
		public int compute(int a, int b) {
			return a % b;
		}

		@Override
		public int neutral() {
			return 1;
		}
	};
	
	/**
	 * Operator character.
	 */
	public final char operator;
	
	private ArithmeticOperator(char operator) {
		this.operator = operator;
	}
	
	/**
	 * Returns the operator corresponding to the specified character.
	 * @param operator
	 */
	public static ArithmeticOperator get(char operator) {
		for (ArithmeticOperator op : ArithmeticOperator.values())
			if (operator == op.operator)
				return op;
		return null;
	}
	
	/**
	 * Returns the neutral value for this operator.
	 * @return 0 if PLUS or MINUS, 1 otherwise
	 */
	public abstract int neutral();
}
