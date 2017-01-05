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

package org.bibliome.util.taxonomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.Pair;
import org.bibliome.util.Strings;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.filelines.TabularFormat;
import org.bibliome.util.filters.AndFilter;
import org.bibliome.util.filters.Filter;
import org.bibliome.util.filters.PatternFilter;
import org.bibliome.util.filters.RejectAll;
import org.bibliome.util.mappers.Mappers;
import org.bibliome.util.mappers.ParamMapper;

public class NCBITaxonomy2 {
	public static final int ROOT_TAXID = 0;
	public static final String SCIENTIFIC_NAME = "scientific name";

	private final int[] parent;
	private final String[] rank;
	private final int[] division;
	private final Collection<Saturator> saturators = new ArrayList<Saturator>();
	private final Collection<Integer> excludedTaxa = new HashSet<Integer>();
	private final Filter<String> excludedNames = new RejectAll<String>();
	private final DefaultMap<Integer,Filter<String>> taxonSpecificExcludedNames = new DefaultExcluded();
	
	public NCBITaxonomy2(int maxId) {
		super();
		this.parent = new int[maxId];
		this.rank = new String[maxId];
		this.division = new int[maxId];
	}
	
	private static final class DefaultExcluded extends DefaultMap<Integer,Filter<String>> {
		private static final Filter<String> NO_EXCLUDE = new RejectAll<String>();
		
		private DefaultExcluded() {
			super(false, new HashMap<Integer,Filter<String>>());
		}

		@Override
		protected Filter<String> defaultValue(Integer key) {
			return NO_EXCLUDE;
		}
	}
	
	public void readNodes(BufferedReader reader) throws IOException {
		TabularFormat format = getNodesDmpFormat();
		List<String> line = new ArrayList<String>(format.getMaxColumns());
		Iterator<List<String>> lineIt = format.tabularIterator(reader);
		while (lineIt.hasNext()) {
			lineIt.next();
			final int id = Integer.parseInt(line.get(0));
			parent[id] = Integer.parseInt(line.get(1));
			rank[id] = line.get(2).intern();
			division[id] = Integer.parseInt(line.get(4));
		}
	}

	private static TabularFormat getNodesDmpFormat() {
		TabularFormat result = new TabularFormat();
		result.setNumColumns(14);
		result.setNullifyEmpty(false);
		result.setSeparator('|');
		result.setSkipBlank(true);
		result.setSkipEmpty(true);
		result.setStrictColumnNumber(true);
		result.setTrimColumns(true);
		return result;
	}
	
	public void printDictionary(BufferedReader reader, PrintStream out) throws IOException {
		TabularFormat format = getNamesFormat();
		Iterator<List<String>> lineIt = format.tabularIterator(reader);
		int currentTaxid = -1;
		List<Pair<String,String>> names = new ArrayList<Pair<String,String>>();
		String canonicalName = null;
		while (lineIt.hasNext()) {
			List<String> line = lineIt.next();
			int taxid = Integer.parseInt(line.get(0));
			if (excludedTaxa.contains(taxid))
				continue;
			String nameType = line.get(3).intern();
			if (taxid == currentTaxid) {
				String name = line.get(1);
				names.add(new Pair<String,String>(name, nameType));
				if (nameType.equals(SCIENTIFIC_NAME))
					canonicalName = name;
			}
			else if (currentTaxid != -1) {
				String path = getPath(currentTaxid);
				Filter<String> excludedNames = taxonSpecificExcludedNames.safeGet(currentTaxid);
				for (Pair<String,String> name : names) {
					handleName(out, name.first, excludedNames, currentTaxid, name.second, canonicalName, path);
					for (Saturator saturator : saturators)
						for (String var : saturator.saturate(name.first))
							handleName(out, var, excludedNames, currentTaxid, saturator.nameType, canonicalName, path);
				}
				currentTaxid = taxid;
				names.clear();
				canonicalName = null;
			}
		}
	}

	private static TabularFormat getNamesFormat() {
		TabularFormat result = new TabularFormat();
		result.setNumColumns(5);
		result.setStrictColumnNumber(true);
		result.setSeparator('|');
		result.setTrimColumns(true);
		result.setSkipBlank(true);
		result.setSkipEmpty(true);
		return result;
	}

	private String getPath(int currentTaxid) {
		List<Integer> ancestors = new ArrayList<Integer>();
		for (int taxid = currentTaxid; taxid != ROOT_TAXID; taxid = parent[taxid])
			ancestors.add(taxid);
		ancestors.add(ROOT_TAXID);
		Collections.reverse(ancestors);
		StringBuilder sb = new StringBuilder();
		for (Integer taxid : ancestors) {
			sb.append('/');
			sb.append(taxid);
		}
		return sb.toString();
	}

	private void handleName(PrintStream out, String name, Filter<String> excludedNames, int taxid, String nameType, String canonicalName, String path) {
		if (excludedNames.accept(name) || this.excludedNames.accept(name))
			return;
		out.print(name);
		out.print('\t');
		out.print(taxid);
		out.print('\t');
		out.print(nameType);
		out.print('\t');
		out.print(canonicalName);
		out.print('\t');
		out.print(path);
		out.println();
	}
	
	private static final class Saturator implements ParamMapper<MessageFormat,String,Matcher> {
		private final Pattern pattern;
		private final String nameType;
		private final Collection<MessageFormat> formats;
		
		private Saturator(Pattern pattern, String nameType, MessageFormat[] formats) {
			super();
			this.pattern = pattern;
			this.nameType = nameType;
			this.formats = Arrays.asList(formats);
		}
		
		@Override
		public String map(MessageFormat param, Matcher m) {
			String[] groups = new String[m.groupCount() + 1];
			for (int i = 0; i < groups.length; ++i)
				groups[i] = m.group(i);
			return param.format(groups);
		}

		private Collection<String> saturate(String name) {
			Matcher m = pattern.matcher(name);
			if (!m.find())
				return Collections.emptyList();
			return Mappers.mappedCollection(this, m, formats);
		}
	}

	public void readSaturators(BufferedReader reader) throws IOException {
		TabularFormat format = getSaturatorFormat();
		List<String> line = new ArrayList<String>();
		Iterator<List<String>> lineIt = format.tabularIterator(reader);
		while (lineIt.hasNext()) {
			lineIt.next();
			Pattern pattern = Pattern.compile(line.get(0));
			String nameType = new String(line.get(1));
			MessageFormat[] formats = new MessageFormat[line.size() - 2];
			for (int i = 0; i < formats.length; ++i)
				formats[i] = new MessageFormat(line.get(i + 2));
			saturators.add(new Saturator(pattern, nameType, formats));
		}
	}

	private static TabularFormat getSaturatorFormat() {
		TabularFormat result = new TabularFormat();
		result.setMinColumns(3);
		result.setSkipBlank(true);
		result.setSkipEmpty(true);
		return result;
	}

	public void readExcluded(BufferedReader reader) throws IOException {
		TabularFormat format = getExcludedFormat();
		List<String> line = new ArrayList<String>(format.getMaxColumns());
		Iterator<List<String>> lineIt = format.tabularIterator(reader);
		while (lineIt.hasNext()) {
			lineIt.next();
			if (line.size() == 1) {
				String s = line.get(0);
				if (Strings.allDigits(s))
					excludedTaxa.add(Integer.parseInt(s));
				else
					excludedNames.accept(s);
			}
			else {
				int taxid = Integer.parseInt(line.get(0));
				Filter<String> patternFilter = new PatternFilter<String>(line.get(1));
				Filter<String> filter;
				if (taxonSpecificExcludedNames.containsKey(taxid)) {
					Filter<String> left = taxonSpecificExcludedNames.get(taxid);
					filter = new AndFilter<String>(left, patternFilter);
				}
				else
					filter = patternFilter;
				taxonSpecificExcludedNames.put(taxid, filter);
			}
		}
	}

	private static TabularFormat getExcludedFormat() {
		TabularFormat result = new TabularFormat();
		result.setMinColumns(1);
		result.setMaxColumns(2);
		result.setSkipBlank(true);
		result.setSkipEmpty(true);
		return result;
	}
}
