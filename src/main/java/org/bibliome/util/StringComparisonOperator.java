package org.bibliome.util;

/**
 * String comparison operators.
 * @author rbossy
 *
 */
public enum StringComparisonOperator {
	EQ {
		@Override
		public boolean accept(String a, String b) {
			return a.equals(b);
		}
	},
	
	NE {
		@Override
		public boolean accept(String a, String b) {
			return !a.equals(b);
		}
	},
	
	CONTAINS {
		@Override
		public boolean accept(String a, String b) {
			return a.contains(b);
		}
	},
	
	STARTS_WITH {
		@Override
		public boolean accept(String a, String b) {
			return a.startsWith(b);
		}
	},
	
	ENDS_WITH {
		@Override
		public boolean accept(String a, String b) {
			return a.endsWith(b);
		}
	};
	
	/**
	 * Returns true iff the comparison is verified for the specified operands.
	 * @param a
	 * @param b
	 */
	public abstract boolean accept(String a, String b);
}
