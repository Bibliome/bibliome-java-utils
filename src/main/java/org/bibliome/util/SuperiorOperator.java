package org.bibliome.util;

public enum SuperiorOperator implements BinaryNumericOperator {
	MIN {
		@Override
		public int compute(int a, int b) {
			return Math.min(a, b);
		}

		@Override
		public double compute(double a, double b) {
			return Math.min(a, b);
		}
	},
	
	MAX {
		@Override
		public int compute(int a, int b) {
			return Math.max(a, b);
		}

		@Override
		public double compute(double a, double b) {
			return Math.max(a, b);
		}
	};
	
	@Override
	public abstract int compute(int a, int b);
	@Override
	public abstract double compute(double a, double b);
}
