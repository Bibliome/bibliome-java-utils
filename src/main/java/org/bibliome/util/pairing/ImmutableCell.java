package org.bibliome.util.pairing;

/**
 * Immutable matrix cell.
 * @author rbossy
 *
 */
class ImmutableCell extends Cell {
	private final int row;
	private final int column;
	
	/**
	 * Creates a new immutable matrix cell at the specified position.
	 * @param row
	 * @param column
	 */
	ImmutableCell(int row, int column) {
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
}
