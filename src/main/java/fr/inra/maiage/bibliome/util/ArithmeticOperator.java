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

package fr.inra.maiage.bibliome.util;

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
