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

package org.bibliome.util.filelines;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;
import org.bibliome.util.filters.Filter;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.ParamMapper;
import org.bibliome.util.streams.SourceStream;

/**
 * A tabular format holds various tabular file format preferences.
 * @author rbossy
 *
 */
public class TabularFormat implements Filter<String>, ParamMapper<String,List<String>,List<String>>, Mapper<String,List<String>> {
	private char separator = '\t';
	private int minColumns = 0;
	private int maxColumns = Integer.MAX_VALUE;
	private int columnLimit = 0;
	private boolean skipEmpty = false;
	private boolean skipBlank = false;
	private boolean trimColumns = false;
	private boolean nullifyEmpty = false;
	private boolean strictColumnNumber = true;

	/**
	 * Creates a tabular format with default preferences.
	 */
	public TabularFormat() {
		super();
	}
	
	/**
	 * Creates a copy of the specified tabular format.
	 * @param format
	 */
	public TabularFormat(TabularFormat format) {
		this();
		separator = format.separator;
		minColumns = format.minColumns;
		maxColumns = format.maxColumns;
		columnLimit = format.columnLimit;
		skipEmpty = format.skipEmpty;
		skipBlank = format.skipBlank;
		trimColumns = format.trimColumns;
		nullifyEmpty = format.nullifyEmpty;
		strictColumnNumber = format.strictColumnNumber;
	}

	/**
	 * Returns the current column separator character.
	 */
	public char getSeparator() {
	    return separator;
	}

	/**
	 * Returns the minimum expected columns.
	 */
	public int getMinColumns() {
	    return minColumns;
	}

	/**
	 * Returns the maximum expected columns.
	 */
	public int getMaxColumns() {
	    return maxColumns;
	}

	/**
	 * Returns either to skip empty lines.
	 */
	public boolean isSkipEmpty() {
	    return skipEmpty;
	}

	/**
	 * Returns either to skip whitespace character only lines.
	 */
	public boolean isSkipBlank() {
	    return skipBlank;
	}

	/**
	 * Returns either columns are stripped from leading and trailing whitespace.
	 */
	public boolean isTrimColumns() {
	    return trimColumns;
	}

	/**
	 * Returns either empty columns are passes as null.
	 */
	public boolean isNullifyEmpty() {
	    return nullifyEmpty;
	}

	/**
	 * Returns either to throw an exception if an entry has a wrong number of columns.
	 */
	public boolean isStrictColumnNumber() {
	    return strictColumnNumber;
	}

	/**
	 * Returns the maximum number of columns each line should be split.
	 */
	public int getColumnLimit() {
		return columnLimit;
	}

	/**
	 * Sets the maximum number of columns each line should be split.
	 * @param columnLimit
	 */
	public void setColumnLimit(int columnLimit) {
		this.columnLimit = columnLimit;
	}

	/**
	 * Sets the column separator character.
	 * @param separator
	 */
	public void setSeparator(char separator) {
	    this.separator = separator;
	}

	/**
	 * Sets the minimum expected columns.
	 * @param minColumns
	 */
	public void setMinColumns(int minColumns) {
	    this.minColumns = minColumns;
	}

	/**
	 * Sets the maximum expected columns.
	 * @param maxColumns
	 */
	public void setMaxColumns(int maxColumns) {
	    this.maxColumns = maxColumns;
	}

	/**
	 * Sets the exact number of expected columns.
	 * @param numColumns
	 */
	public void setNumColumns(int numColumns) {
	    setMinColumns(numColumns);
	    setMaxColumns(numColumns);
	}

	/**
	 * Sets either to skip empty lines.
	 * @param skipEmpty
	 */
	public void setSkipEmpty(boolean skipEmpty) {
	    this.skipEmpty = skipEmpty;
	}

	/**
	 * Sets either to skip whitespace character only lines.
	 * @param skipBlank
	 */
	public void setSkipBlank(boolean skipBlank) {
	    this.skipBlank = skipBlank;
	}

	/**
	 * Sets either to trim leading and trailing whitespace from each column.
	 * @param trimColumns
	 */
	public void setTrimColumns(boolean trimColumns) {
	    this.trimColumns = trimColumns;
	}

	/**
	 * Sets either to pass empty column as null.
	 * @param nullifyEmpty
	 */
	public void setNullifyEmpty(boolean nullifyEmpty) {
	    this.nullifyEmpty = nullifyEmpty;
	}

	/**
	 * Sets either to enforce the column number.
	 * @param strictColumnNumber
	 */
	public void setStrictColumnNumber(boolean strictColumnNumber) {
	    this.strictColumnNumber = strictColumnNumber;
	}

	@Override
	public List<String> map(String x) {
		return map(x, null);
	}

	@Override
	public List<String> map(String x, List<String> param) {
		List<String> result = Strings.split(x, separator, columnLimit, param);
		if (strictColumnNumber) {
			if (result.size() < minColumns)
				throw new RuntimeException();
			if (result.size() > maxColumns)
				throw new RuntimeException();
		}
		for (int i = 0; i < result.size(); ++i) {
			if (trimColumns)
				result.set(i, result.get(i).trim());
			if (nullifyEmpty && result.get(i).isEmpty())
				result.set(i, null);
		}
		return result;
	}

	@Override
	public boolean accept(String x) {
		if (skipEmpty && x.isEmpty())
			return false;
		if (skipBlank && x.trim().isEmpty())
			return false;
		return true;
	}

	public Iterator<List<String>> tabularIterator(BufferedReader reader, List<String> entry) throws IOException {
		return Iterators.filterAndMap(this, this, entry, new LineIterator(reader));
	}
	
	public Iterator<List<String>> tabularIterator(BufferedReader reader) throws IOException {
		return Iterators.filterAndMap(this, this, new LineIterator(reader));
	}
	
	
	
	
	
	
	
	
	
	
	private final class SourceStreamLineIteratorMapper implements Mapper<BufferedReader,TabularLineIterator> {
		private final SourceStream sourceStream;
		private final boolean reuse;
		
		private SourceStreamLineIteratorMapper(SourceStream sourceStream, boolean reuse) {
			super();
			this.sourceStream = sourceStream;
			this.reuse = reuse;
		}

		@Override
		public TabularLineIterator map(BufferedReader x) {
			String source = sourceStream.getStreamName(x);
			return new TabularLineIterator(source, x, reuse, true);
		}
	}
		
	private void fillLine(TabularLine tabularLine, String line) {
		Strings.split(line, separator, columnLimit, tabularLine);
	}
	
	private void checkTabularLine(TabularLine tabularLine) {
		if (strictColumnNumber && tabularLine.size() < minColumns)
			throw new RuntimeException();
		if (strictColumnNumber && tabularLine.size() > maxColumns)
			throw new RuntimeException();
		if (trimColumns || nullifyEmpty) {
			for (int i = 0; i < tabularLine.size(); ++i) {
				String s0 = tabularLine.get(i);
				String s = s0;
				if (trimColumns) {
					s = s.trim();
				}
				if (nullifyEmpty && s.isEmpty()) {
					s = null;
				}
				tabularLine.set(i, s);
			}
		}
	}
	
	private final class TabularLineIterator implements Iterator<TabularLine> {
		private final BufferedReader reader;
		private final TabularLine currentLine;
		private String nextLine;
		private final boolean reuse;
		private final boolean close;
		
		private TabularLineIterator(String source, BufferedReader reader, boolean reuse, boolean close) {
			super();
			this.currentLine = new TabularLine(source);
			this.reader = reader;
			this.reuse = reuse;
			this.close = close;
		}
		
		@Override
		public boolean hasNext() {
			if (nextLine != null) {
				return true;
			}
			try {
				while (true) {
					nextLine = reader.readLine();
					if (nextLine == null) {
						if (close) {
							reader.close();
						}
						return false;
					}
					if (skipEmpty && nextLine.isEmpty()) {
						continue;
					}
					if (skipBlank && nextLine.trim().isEmpty()) {
						continue;
					}
					return true;
				}
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		private TabularLine getNextTabularLine() {
			if (reuse) {
				currentLine.incrLineno();
				return currentLine;
			}
			return new TabularLine(currentLine.getSource(), currentLine.getLineno() + 1);
		}

		@Override
		public TabularLine next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			TabularLine result = getNextTabularLine();
			fillLine(result, nextLine);
			nextLine = null;
			checkTabularLine(result);
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public Iterator<TabularLine> iterator(String source, BufferedReader reader, boolean reuse) {
		return new TabularLineIterator(source, reader, reuse, false);
	}
	
	public Iterator<TabularLine> iterator(SourceStream sourceStream, boolean reuse) throws IOException {
		SourceStreamLineIteratorMapper mapper = new SourceStreamLineIteratorMapper(sourceStream, reuse);
		Iterator<BufferedReader> readerIt = sourceStream.getBufferedReaders();
		return Iterators.mapAndFlatten(readerIt, mapper);
	}
}
