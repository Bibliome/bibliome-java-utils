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

package fr.inra.maiage.bibliome.util.yatea.pattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.inra.maiage.bibliome.util.yatea.pattern.parser.ParseException;
import fr.inra.maiage.bibliome.util.yatea.pattern.parser.ParsingPatternParser;

import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

public class ParsingPattern {
	private final String source;
	private final int lineno;
	private final Chunk chunk;
	private final int priority;
	private final TermDirection direction;
	
	public ParsingPattern(String source, int lineno, Chunk chunk, int priority, TermDirection direction) {
		super();
		this.source = source;
		this.lineno = lineno;
		this.chunk = chunk;
		this.priority = priority;
		this.direction = direction;
	}

	public String getSource() {
		return source;
	}

	public int getLineno() {
		return lineno;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public int getPriority() {
		return priority;
	}

	public TermDirection getDirection() {
		return direction;
	}

	private static class LineParser {
		private final ParsingPatternParser parser = new ParsingPatternParser((Reader) null);
		private final List<String> columns = new ArrayList<String>(3);
		private String source;
		private int lineno;
		
		private ParsingPattern parseLine(String line) throws ParseException {
			Strings.split(line, '\t', 3, columns);
			if (columns.size() < 3) {
				throw new RuntimeException();
			}
			
			String patternString = columns.get(0);
			Reader patternReader = new StringReader(patternString);
			parser.ReInit(patternReader);
			Chunk chunk = parser.chunk();
			
			String priorityString = columns.get(1);
			int priority = Integer.parseInt(priorityString);
			
			String directionString = columns.get(2);
			TermDirection direction = TermDirection.valueOf(directionString);
			
			return new ParsingPattern(source, lineno, chunk, priority, direction);
		}
		
		private void parseLines(BufferedReader reader, Collection<ParsingPattern> patterns) throws ParseException, IOException {
			lineno = 0;
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				lineno++;
				line = line.trim();
				if (line.isEmpty() || line.charAt(0) == '#') {
					continue;
				}
				ParsingPattern pattern = parseLine(line);
				patterns.add(pattern);
			}
		}
	}

	public static Collection<ParsingPattern> parseSource(SourceStream sourceStream) throws ParseException, IOException {
		LineParser parser = new LineParser();
		Collection<ParsingPattern> result = new ArrayList<ParsingPattern>();
		for (BufferedReader reader : Iterators.loop(sourceStream.getBufferedReaders())) {
			parser.source = sourceStream.getStreamName(reader);
			parser.parseLines(reader, result);
		}
		return result;
	}
}
