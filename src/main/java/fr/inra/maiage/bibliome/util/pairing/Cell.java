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

package fr.inra.maiage.bibliome.util.pairing;

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
