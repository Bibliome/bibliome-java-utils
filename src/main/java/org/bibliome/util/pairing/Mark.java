package org.bibliome.util.pairing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bibliome.util.defaultmap.DefaultHashSetHashMap;
import org.bibliome.util.defaultmap.DefaultMap;

class Mark {
	private final DefaultMap<Integer,Set<Integer>> rows = new DefaultHashSetHashMap<Integer,Integer>();
	private final DefaultMap<Integer,Set<Integer>> columns = new DefaultHashSetHashMap<Integer,Integer>();

	boolean isRowMarked(int i) {
		return rows.containsKey(i);
	}
	
	boolean isColumnMarked(int j) {
		return columns.containsKey(j);
	}
	
	void mark(int i, int j) {
		rows.safeGet(i).add(j);
		columns.safeGet(j).add(i);
	}
	
	void unmark(int i, int j) {
		unmark(rows, i, j);
		unmark(columns, j, i);
	}
	
	private static void unmark(DefaultMap<Integer,Set<Integer>> line, int k, int l) {
		if (!line.containsKey(k))
			return;
		Set<Integer> cross = line.get(k);
		cross.remove(l);
		if (cross.isEmpty())
			line.remove(k);
	}
	
	Collection<Integer> getMarkedRows() {
		return rows.keySet();
	}
	
	Collection<Integer> getMarkedColumns() {
		return columns.keySet();
	}
	
	Integer columnMarkedInRow(int i) {
		if (rows.containsKey(i))
			return rows.get(i).iterator().next();
		return null;
	}
	
	Integer rowMarkedInColumn(int j) {
		if (columns.containsKey(j))
			return columns.get(j).iterator().next();
		return null;
	}
	
	void clear() {
		rows.clear();
		columns.clear();
	}
		
	Collection<Cell> allMarked(boolean transposed) {
		Collection<Cell> result = new ArrayList<Cell>();
		Map<Integer,Set<Integer>> line = transposed ? columns : rows;
		for (Map.Entry<Integer,Set<Integer>> e : line.entrySet()) {
			int i = e.getKey();
			int j = e.getValue().iterator().next();
			result.add(new ImmutableCell(i, j));
		}
		return result;
	}
}
