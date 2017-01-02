package org.bibliome.util.pairing;

/**
 * Matrix cell.
 * @author rbossy
 *
 */
public abstract class Cell {
	/**
	 * Returns the value of this cell given the specified matrix.
	 * @param matrix
	 */
	public double getValue(double[][] matrix) {
		return matrix[getRow()][getColumn()];
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [row=" + getRow() + ", column=" + getColumn() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getColumn();
		result = prime * result + getRow();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ImmutableCell))
			return false;
		Cell other = (Cell) obj;
		if (getColumn() != other.getColumn())
			return false;
		if (getRow() != other.getRow())
			return false;
		return true;
	}

	/**
	 * Returns this cell row number.
	 */
	public abstract int getRow();
	
	/**
	 * Returns this cell column number.
	 */
	public abstract int getColumn();
}
