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

package org.bibliome.util.pairing;

/**
 * Mutable position cell.
 * @author rbossy
 *
 */
public class MutableCell extends Cell {
	private int row;
	private int column;
	
	/**
	 * Creates a new mutable position matrix cell.
	 * @param row
	 * @param column
	 */
	MutableCell(int row, int column) {
		super();
		this.row = row;
		this.column = column;
	}

	@Override
	public int getRow() {
		return row;
	}
	
	@Override
	public int getColumn() {
		return column;
	}
	
	/**
	 * Sets this cell row.
	 * @param row
	 */
	public void setRow(int row) {
		this.row = row;
	}
	
	/**
	 * Sets this cell column.
	 * @param column
	 */
	public void setColumn(int column) {
		this.column = column;
	}
	
	/**
	 * Sets this cell position.
	 * @param row
	 * @param column
	 */
	public void setPosition(int row, int column) {
		setRow(row);
		setColumn(column);
	}
}
