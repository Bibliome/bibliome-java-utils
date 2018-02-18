package fr.inra.maiage.bibliome.util.aggregate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.util.filelines.FileLines;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.filelines.TabularFormat;
import fr.inra.maiage.bibliome.util.mappers.Mappers;

public class AggregatorFileLines extends FileLines<Map<List<String>,List<Aggregator>>> {
	private final List<AggregatorFactory> specification;
	private final int[] groupByIndexes;
	private final String[] currentItem;
	private final List<String> currentItemList;
	
	public AggregatorFileLines(TabularFormat format, List<AggregatorFactory> specification) {
		super(format);
		this.specification = specification;
		this.groupByIndexes = buildGroupByIndexes(specification);
		this.currentItem = new String[this.groupByIndexes.length];
		this.currentItemList = Arrays.asList(this.currentItem);
	}
	
	private static int[] buildGroupByIndexes(List<AggregatorFactory> specification) {
		int groupByCount = countGroupBy(specification);
		int[] result = new int[groupByCount];
		int j = 0;
		for (int i = 0; i < specification.size(); ++i) {
			AggregatorFactory aggf = specification.get(i);
			if (aggf == GroupBy.INSTANCE) {
				result[j] = i;
				j++;
			}
		}
		return result;
	}
	
	private static int countGroupBy(List<AggregatorFactory> specification) {
		int result = 0;
		for (AggregatorFactory aggf : specification) {
			if (aggf == GroupBy.INSTANCE) {
				result++;
			}
		}
		return result;
	}
	
	@Override
	public void processEntry(Map<List<String>,List<Aggregator>> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
		updateCurrentItem(entry);
		List<Aggregator> item = getItem(data);
		updateItem(item, entry);
	}

	private void updateCurrentItem(List<String> entry) {
		for (int i = 0; i < groupByIndexes.length; ++i) {
			int j = groupByIndexes[i];
			currentItem[i] = entry.size() >= j ? entry.get(j): "";
		}
	}
	
	private List<Aggregator> getItem(Map<List<String>,List<Aggregator>> data) {
		if (data.containsKey(currentItemList)) {
			return data.get(currentItemList);
		}
		List<String> key = new ArrayList<String>(currentItemList);
		List<Aggregator> result = Mappers.apply(AggregatorFactory.MAPPER, specification, new ArrayList<Aggregator>(specification.size()));
		data.put(key, result);
		return result;
	}
	
	private static void updateItem(List<Aggregator> item, List<String> entry) {
		Iterator<Aggregator> aggIt = item.iterator();
		Iterator<String> valueIt = entry.iterator();
		while (valueIt.hasNext()) {
			if (!aggIt.hasNext()) {
				break;
			}
			Aggregator agg = aggIt.next();
			String value = valueIt.next();
			agg.add(value);
		}
	}
}
