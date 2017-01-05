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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class implementing the Hungarian assignment algorithm.
 * @author rbossy
 *
 */
public class Munkres {
	/**
	 * Original cost matrix.
	 */
	public final double[][] matrix;
	private final double[][] workMatrix;
	private final boolean transposed;
	private final Set<Integer> coveredRows = new HashSet<Integer>();
	private final Set<Integer> coveredColumns = new HashSet<Integer>();
	private final Mark star = new Mark();
	private final Mark prime = new Mark();

	private Munkres(double[][] matrix, boolean invert, boolean checkShape, boolean checkNonnegative) {
		if (checkShape)
			checkShape(matrix);
		if (checkNonnegative)
			checkNonnegative(matrix);
		this.matrix = matrix;
		transposed = matrix[0].length < matrix.length;
		workMatrix = transposed ? transposeMatrix(matrix) : copyMatrix(matrix);
		if (invert)
			invertScores(workMatrix);
	}

	/**
	 * Creates a new Munkres object.
	 * @param matrix cost matrix
	 * @param invert either the cost matrix is in fact a similarity matrix
	 */
	public Munkres(double[][] matrix, boolean invert) {
		this(matrix, invert, true, true);
	}
	
	/**
	 * Creates a new Munkres object with the specified cost matrix.
	 * @param matrix
	 */
	public Munkres(double[][] matrix) {
		this(matrix, false);
	}
	
	/**
	 * Creates a new Munkres object for the specified tasks/workers and cost function.
	 * @param rows
	 * @param cols
	 * @param score
	 * @param invert true if score is not a cost but a similarity
	 */
	public <A,B> Munkres(List<A> rows, List<B> cols, Score<A,B> score, boolean invert) {
		this(buildMatrix(rows, cols, score), invert, false, true);
	}
	
	/**
	 * Builds a cost matrix from the specified tasks/workers and cost function.
	 * @param rows
	 * @param cols
	 * @param score
	 * @return the cost matrix.
	 */
	public static <A,B> double[][] buildMatrix(List<A> rows, List<B> cols, Score<A,B> score) {
		double[][] result = new double[rows.size()][cols.size()];
		for (int i = 0; i < result.length; ++i) {
			double[] row = result[i];
			A a = rows.get(i);
			for (int j = 0; j < row.length; ++j)
				row[j] = score.getScore(a, cols.get(j));
		}
		return result;
	}
	
	private static void checkShape(double[][] matrix) {
		final int nRows = matrix.length;
		if (nRows == 0)
			throw new IllegalArgumentException("matrix.length == 0");
		int nCols = matrix[0].length;
		if (nCols == 0)
			throw new IllegalArgumentException("matrix[0].length == 0");
		for (int i = 1; i < nRows; ++i)
			if (matrix[i].length != nCols)
				throw new IllegalArgumentException("matrix[" + i + "].length != matrix[0].length");
	}
	
	private static void checkNonnegative(double[][] matrix) {
		for (double[] row : matrix)
			for (double v : row)
				if (v < 0)
					throw new IllegalArgumentException("negative value");
	}
	
	private static double[][] copyMatrix(double[][] matrix) {
		double[][] result = new double[matrix.length][];
		for (int i = 0; i < result.length; ++i)
			result[i] = Arrays.copyOf(matrix[i], matrix[i].length);
		return result;
	}
	
	private static double[][] transposeMatrix(double[][] matrix) {
		double[][] result = new double[matrix[0].length][matrix.length];
		for (int i = 0; i < result.length; ++i) {
			double[] row = result[i];
			for (int j = 0; j < row.length; ++j)
				row[j] = matrix[j][i];
		}
		return result;
	}
	
	private static void invertScores(double[][] matrix) {
		for (int i = 0; i < matrix.length; ++i) {
			double[] row = matrix[i];
			for (int j = 0; j < row.length; ++j)
				row[j] = 1 / row[j];
		}
	}

	/**
	 * Solve this object.
	 * @return cells that indicate the optimal pairing.
	 */
	public Collection<Cell> solve() {
		return step1();
	}
	
	private Collection<Cell> step1() {
		for (double[] row : workMatrix) {
			double min = rowMin(row);
			substractFromRow(row, min);
		}
		step2();
		return star.allMarked(transposed);
	}

	private static void substractFromRow(double[] row, double v) {
		for (int i = 0; i < row.length; ++i)
			row[i] -= v;
	}

	private static double rowMin(double[] row) {
		double result = Double.MAX_VALUE;
		for (double v : row)
			if (v < result)
				result = v;
		return result;
	}

	private void step2() {
		ROWS: for (int i = 0; i < workMatrix.length; ++i) {
			if (star.isRowMarked(i))
				continue;
			double[] row = workMatrix[i];
			for (int j = 0; j < row.length; ++j) {
				if (row[j] != 0)
					continue;
				if (star.isColumnMarked(j))
					continue;
				star.mark(i, j);
				continue ROWS;
			}
		}
		step3();
	}
	
	private void step3() {
		coveredColumns.addAll(star.getMarkedColumns());
		if (coveredColumns.size() < workMatrix.length)
			step4();
	}

	private void step4() {
		ROWS: for (int i = 0; i < workMatrix.length; ++i) {
			if (coveredRows.contains(i))
				continue;
			double[] row = workMatrix[i];
			for (int j = 0; j < row.length; ++j) {
				if (coveredColumns.contains(j))
					continue;
				if (row[j] != 0)
					continue;
				prime.mark(i, j);
				if (star.isRowMarked(i)) {
					coveredRows.add(i);
					coveredColumns.remove(star.columnMarkedInRow(i));
					continue ROWS;
				}
				else {
					step5(new ImmutableCell(i, j));
					return;
				}
			}
		}
		step6();
	}
	
	private void step5(ImmutableCell primed) {
		Collection<ImmutableCell> primedSeries = new ArrayList<ImmutableCell>();
		Collection<ImmutableCell> starredSeries = new ArrayList<ImmutableCell>();
		while (true) {
			primedSeries.add(primed);
			Integer starredRow = star.rowMarkedInColumn(primed.getColumn());
			if (starredRow == null)
				break;
			ImmutableCell starred = new ImmutableCell(starredRow, primed.getColumn());
			starredSeries.add(starred);
			primed = new ImmutableCell(starred.getRow(), prime.columnMarkedInRow(starred.getRow()));
		}
		for (ImmutableCell s : starredSeries)
			star.unmark(s.getRow(), s.getColumn());
		for (ImmutableCell p : primedSeries)
			star.mark(p.getRow(), p.getColumn());
		prime.clear();
		uncover();
		step3();
	}
	
	private void uncover() {
		coveredRows.clear();
		coveredColumns.clear();
	}
	
	private void step6() {
		double min = minUncovered();
		for (int i = 0; i < workMatrix.length; ++i) {
			double[] row = workMatrix[i];
			if (coveredRows.contains(i)) {
				for (int j : coveredColumns)
					row[j] += min;
			}
			else {
				for (int j = 0; j < row.length; ++j) {
					if (coveredColumns.contains(j))
						continue;
					row[j] -= min;
				}
			}
		}
		step4();
	}

	private double minUncovered() {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < workMatrix.length; ++i) {
			if (coveredRows.contains(i))
				continue;
			double[] row = workMatrix[i];
			for (int j = 0; j < row.length; ++j) {
				if (coveredColumns.contains(j))
					continue;
				double v = row[j];
				if (v < min)
					min = v;
			}
		}
		return min;
	}
}
