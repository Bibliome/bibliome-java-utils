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

package org.bibliome.util.pattern.tabular;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.bibliome.util.Iterators;
import org.bibliome.util.StringComparisonOperator;
import org.bibliome.util.Strings;
import org.bibliome.util.clio.CLIOException;
import org.bibliome.util.clio.CLIOParser;
import org.bibliome.util.clio.CLIOption;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.filelines.TabularFormat;
import org.bibliome.util.mappers.Mappers;
import org.bibliome.util.mappers.ParamMapper;
import org.bibliome.util.pattern.CapturingGroup;
import org.bibliome.util.pattern.SequenceMatcher;
import org.bibliome.util.pattern.SequencePattern;
import org.bibliome.util.pattern.tabular.expression.BooleanConstant;
import org.bibliome.util.pattern.tabular.expression.StringComparison;
import org.bibliome.util.pattern.tabular.expression.StringConstant;
import org.bibliome.util.pattern.tabular.expression.StringReference;
import org.bibliome.util.pattern.tabular.parser.ParseException;
import org.bibliome.util.pattern.tabular.parser.TabularPatternParser;
import org.bibliome.util.streams.CollectionSourceStream;
import org.bibliome.util.streams.CompressionFilter;
import org.bibliome.util.streams.InputStreamSourceStream;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.StreamFactory;
import org.bibliome.util.yatea.pattern.ChunkSequencePatternMapper;
import org.bibliome.util.yatea.pattern.ParsingPattern;

public class TabularPattern extends CLIOParser {
	private static final String[] ANSI_COLORS = {
		"\u001B[31m",
		"\u001B[32m",
		"\u001B[33m",
		"\u001B[34m",
		"\u001B[35m",
		"\u001B[36m",
		"\u001B[37m"
	};
	private static final String ANSI_RESET = "\u001B[0m";
	
	// pattern parser
	private final TabularPatternParser parser = new TabularPatternParser((Reader) null);

	// mandatory options: input and pattern
	private final StreamFactory streamFactory = new StreamFactory();
	private final List<String> input = new ArrayList<String>();
	private SequencePattern<List<String>,TabularContext,TabularExpression> pattern;
	private List<CapturingGroup<List<String>,TabularContext,TabularExpression>> groups;

	// search options
	private final TabularContext context = new TabularContext();
	private TabularExpression sentenceFrontier = BooleanConstant.FALSE;
	private final TabularFormat format = new TabularFormat();
	private boolean yateaPatterns = false;

	// output options
	private OutputContext outputContext = OutputContext.NONE;
	private boolean headerLine = false;
	private boolean colors = false;
	private boolean printFilename = false;
	private boolean printLineno = false;
	private String printGroupName = null;
	
	private final Map<String,List<String>> profiles = new TreeMap<String,List<String>>();

	public TabularPattern() throws IOException {
		super();
		try (InputStream is = TabularPattern.class.getResourceAsStream("TabularPattern.properties")) {
			if (is != null) {
				Properties props = new Properties();
				props.load(is);
				loadProfiles(props);
			}
		}
	}

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		try {
			if (pattern == null) {
				Reader reader = new StringReader(arg);
				loadPattern(reader);
			}
			else {
				input.add(arg);
			}
		}
		catch (ParseException e) {
			throw new CLIOException(e);
		}
		return false;
	}
	
	private void loadPattern(Reader reader) throws ParseException {
		parser.ReInit(reader);
		pattern = parser.pattern();
		groups = pattern.getCapturingGroups();
	}
	
	private void loadProfiles(Properties props) {
		for (String name : props.stringPropertyNames()) {
			String v = props.getProperty(name);
			List<String> values = Strings.splitAndTrim(v, ',', 0);
			profiles.put(name, values);
		}
	}

	@Override
	public String getResourceBundleName() {
		return TabularPattern.class.getCanonicalName() + "Help";
	}

	private final ChunkSequencePatternMapper<List<String>,TabularContext,TabularExpression> MAPPER = new ChunkSequencePatternMapper<List<String>,TabularContext,TabularExpression>() {
		@Override
		protected TabularExpression getLemmaFilter(String value) {
			TabularExpression ref = new StringReference("lemma");
			TabularExpression val = new StringConstant(value);
			return new StringComparison(StringComparisonOperator.EQ, ref, val);
		}

		@Override
		protected TabularExpression getPOSFilter(String value) {
			TabularExpression ref = new StringReference("pos");
			TabularExpression val = new StringConstant(value);
			return new StringComparison(StringComparisonOperator.EQ, ref, val);
		}
	};

	@CLIOption(value="-help", stop=true)
	public void help() { 
		System.out.print(usage());
	}

	@CLIOption("-yateaPatterns")
	public void loadYateaPatterns(String file) throws IOException, URISyntaxException, org.bibliome.util.yatea.pattern.parser.ParseException {
		yateaPatterns = true;
		SourceStream source = streamFactory.getSourceStream(file);
		Collection<ParsingPattern> patterns = ParsingPattern.parseSource(source);
		pattern = MAPPER.getSequencePattern(patterns);
		groups = pattern.getCapturingGroups();
		setProfile("tree-tagger");
	}

	@CLIOption("-sentence")
	public void setSentenceFrontier(String expr) throws ParseException {
		Reader reader = new StringReader(expr);
		parser.ReInit(reader);
		sentenceFrontier = parser.expression();
	}

	@CLIOption("-pattern")
	public void loadPatternFile(String file) throws ParseException, IOException, URISyntaxException {
		SourceStream source = streamFactory.getSourceStream(file);
		try (Reader reader = source.getBufferedReader()) {
			loadPattern(reader);
		}
	}
		
	@CLIOption("-header")
	public void setHeader(String name, int column) {
		context.setColumnName(name, column);
	}
	
	@CLIOption("-headerLine")
	public void setHeaderLine() {
		headerLine  = true;
	}
	
	@CLIOption("-profile")
	public void setProfile(String name) {
		if (!profiles.containsKey(name)) {
			throw new RuntimeException();
		}
		List<String> header = profiles.get(name);
		for (int i = 0; i < header.size(); ++i) {
			String cn = header.get(i);
			context.setColumnName(cn, i);
		}
	}
	
	@CLIOption(value="-listProfiles", stop=true)
	public void listProfiles() {
		for (Map.Entry<String,List<String>> e : profiles.entrySet()) {
			System.out.print(e.getKey());
			System.out.print(':');
			for (String name : e.getValue()) {
				System.out.print(' ');
				System.out.print(name);
			}
			System.out.println();
		}
	}
	
	@CLIOption("-color")
	public void setColors() {
		colors = true;
	}
	
	@CLIOption("-separator")
	public void setSeparator(char sep) {
		format.setSeparator(sep);
	}
	
	@CLIOption("-filename")
	public void setPrintFilename() {
		printFilename = true;
	}
	
	@CLIOption("-lineno")
	public void setPrintLineno() {
		printLineno = true;
	}
	
	@CLIOption("-group")
	public void setPrintGroupName(String topGroup) {
		printGroupName = topGroup;
	}
	
	@CLIOption("-context")
	public void setContext(String ctxStr) {
		outputContext = OutputContext.valueOf(ctxStr.toUpperCase());
	}

	@CLIOption("-minColumns")
	public void setMinColumns(int n) {
		format.setMinColumns(n);
		format.setStrictColumnNumber(true);
	}
	
	@CLIOption("-maxColumns")
	public void setMaxColumns(int n) {
		format.setMaxColumns(n);
		format.setStrictColumnNumber(true);
	}
	
	@CLIOption("-nColumns")
	public void setNColumns(int n) {
		format.setMinColumns(n);
		format.setMaxColumns(n);
		format.setStrictColumnNumber(true);
	}
	
	@CLIOption("-skipEmpty")
	public void skipEmpty() {
		format.setSkipEmpty(true);
	}
	
	private SourceStream getSourceStream() throws IOException, URISyntaxException {
		if (input.isEmpty()) {
			return new InputStreamSourceStream(streamFactory.getCharset(), CompressionFilter.NONE, System.in, "<<stdin>>");
		}
		if (input.size() == 1) {
			String file = input.get(0);
			return streamFactory.getSourceStream(file);
		}
		Collection<SourceStream> sources = Mappers.mappedCollection(STREAM_MAPPER, streamFactory, input);
		return new CollectionSourceStream(streamFactory.getCharset(), sources);
	}
	
	private static final ParamMapper<String,SourceStream,StreamFactory> STREAM_MAPPER = new ParamMapper<String,SourceStream,StreamFactory>() {
		@Override
		public SourceStream map(String x, StreamFactory param) {
			try {
				return param.getSourceStream(x);
			}
			catch (IOException | URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}
	};
	
	private TabularPatternFileLines getFileLines(PrintStream out) {
		return new TabularPatternFileLines(out);
	}
	
	private class TabularPatternFileLines extends FileLines<String> {
		private final PrintStream out;
		private final List<List<String>> sentence = new ArrayList<List<String>>();
		private boolean readHeader = TabularPattern.this.headerLine;
		private int sentenceLineno;

		private TabularPatternFileLines(PrintStream out) {
			super();
			this.out = out;
		}

		@Override
		public void processEntry(String data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			if (readHeader) {
				for (int i = 0; i < entry.size(); ++i) {
					context.setColumnName(entry.get(i), i);
				}
				readHeader = false;
				return;
			}
			if (sentence.isEmpty()) {
				sentenceLineno = lineno;
			}
			sentence.add(entry);
			if (sentenceFrontier.accept(entry, context)) {
				try {
					searchInSentence(data);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
				sentence.clear();
			}
		}
		
		private void searchInSentence(String source) throws IOException {
			SequenceMatcher<List<String>> matcher = pattern.getMatcher(sentence, context);
			int lastEnd = 0;
			boolean hasMatch = false;
			while (matcher.next()) {
				hasMatch = true;
				int mStart = matcher.getStartIndex();
				int mEnd = matcher.getEndIndex();
				if (outputContext != OutputContext.NONE) {
					printNonMatchColumns(source, lastEnd, mStart);
				}
				int[] inGroup = getMatchGroupSpans(matcher, mStart, mEnd);
				for (int i = mStart; i < mEnd; ++i) {
					printColumn(source, i, inGroup[i - mStart]);
				}
				lastEnd = mEnd;
			}
			if (outputContext == OutputContext.ALL || (outputContext == OutputContext.SENTENCE && hasMatch)) {
				printNonMatchColumns(source, lastEnd, sentence.size());
			}
		}
		
		private int[] getMatchGroupSpans(SequenceMatcher<List<String>> matcher, int mStart, int mEnd) {
			int[] result = new int[mEnd - mStart];
			for (int g = 1; g <= groups.size(); ++g) {
				int gStart = matcher.getStartIndex(g) - mStart;
				int gEnd = matcher.getEndIndex(g) - mStart;
				for (int i = gStart; i < gEnd; ++i) {
					result[i] = g;
				}
			}
			return result;
		}
		
		private void printNonMatchColumns(String source, int from, int to) throws IOException {
			for (int i = from; i < to; ++i) {
				printColumn(source, i, -1);
			}
		}
		
		private int getColor(int g) {
			if (yateaPatterns) {
				CapturingGroup<List<String>,TabularContext,TabularExpression> group = groups.get(g);
				String groupName = group.getName();
				int colon = groupName.lastIndexOf(':');
				if (colon == -1) {
					throw new RuntimeException();
				}
				String role = groupName.substring(colon + 1);
				switch (role) {
					case "":
						return 0;
					case "H1":
						return 1;
					case "M1":
						return 2;
					case "H1H1":
						return 3;
					case "M1H1":
						return 4;
					default:
						throw new RuntimeException("unknown role " + role);
				}
			}
			return g % ANSI_COLORS.length;
		}
		
		private void printColumn(String source, int i, int g) throws IOException {
			List<String> columns = sentence.get(i);
			if (colors && g != -1) {
				int color = getColor(g);
				out.print(ANSI_COLORS[color]);
			}
			char sep = format.getSeparator();
			if (printFilename) {
				out.print(source);
				out.print(sep);
			}
			if (printLineno) {
				out.print(sentenceLineno + i);
				out.print(sep);
			}
			if (printGroupName != null) {
				String name = getGroupName(g);
				out.print(name);
				out.print(sep);
			}
			Strings.join(out, columns, sep);
			if (colors && g != -1) {
				out.print(ANSI_RESET);
			}
			out.println();
		}
		
		private String getGroupName(int g) {
			switch (g) {
				case -1: return "";
				case 0: return printGroupName;
				default: return groups.get(g - 1).getName();
			}
		}
	}

	public static void main(String args[]) throws CLIOException, IOException, URISyntaxException {
		TabularPattern instance = new TabularPattern();
		if (instance.parse(args)) {
			return;
		}
		TabularPatternFileLines fl = instance.getFileLines(System.out);
		SourceStream source = instance.getSourceStream();
		for (BufferedReader reader : Iterators.loop(source.getBufferedReaders())) {
			String name = source.getStreamName(reader);
			fl.process(reader, name);
			fl.searchInSentence(name);
		}
	}
}
