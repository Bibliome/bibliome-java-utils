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
