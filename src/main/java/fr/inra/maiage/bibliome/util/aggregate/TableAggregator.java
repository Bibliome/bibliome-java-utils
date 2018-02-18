package fr.inra.maiage.bibliome.util.aggregate;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.aggregate.aggregators.CollectionAggregator;
import fr.inra.maiage.bibliome.util.aggregate.aggregators.CollectionAggregator.CollectionFactory;
import fr.inra.maiage.bibliome.util.aggregate.aggregators.CountEmpty;
import fr.inra.maiage.bibliome.util.aggregate.aggregators.CountNonEmpty;
import fr.inra.maiage.bibliome.util.aggregate.aggregators.CountValues;
import fr.inra.maiage.bibliome.util.aggregate.aggregators.First;
import fr.inra.maiage.bibliome.util.aggregate.aggregators.Max;
import fr.inra.maiage.bibliome.util.aggregate.aggregators.Mean;
import fr.inra.maiage.bibliome.util.aggregate.aggregators.Min;
import fr.inra.maiage.bibliome.util.aggregate.aggregators.Sum;
import fr.inra.maiage.bibliome.util.clio.CLIOException;
import fr.inra.maiage.bibliome.util.clio.CLIOParser;
import fr.inra.maiage.bibliome.util.clio.CLIOption;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.filelines.TabularFormat;
import fr.inra.maiage.bibliome.util.streams.CompressionFilter;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.InputStreamSourceStream;

public class TableAggregator extends CLIOParser {
	private List<AggregatorFactory> specification = new ArrayList<AggregatorFactory>();
	private File input;
	private File output;
	private String charset = "UTF-8";
	private final TabularFormat format = new TabularFormat();
	private String outputSeparator = "\t";

	public TableAggregator() {
		super();
		format.setNullifyEmpty(false);
		format.setSeparator('\t');
		format.setSkipBlank(false);
		format.setSkipEmpty(false);
		format.setStrictColumnNumber(true);
		format.setTrimColumns(false);
	}

	public void run() throws InvalidFileLineEntry, IOException {
		Collection<List<Aggregator>> aggregatedTable = processTable();
		printAggregatedTable(aggregatedTable);
	}
	
	private TabularFormat getFormat() {
		TabularFormat result = new TabularFormat(format);
		result.setNumColumns(specification.size());
		return result;
	}
	
	private Collection<List<Aggregator>> processTable() throws InvalidFileLineEntry, IOException {
		AggregatorFileLines fl = new AggregatorFileLines(getFormat(), specification);
		Map<List<String>,List<Aggregator>> data = new LinkedHashMap<List<String>,List<Aggregator>>();
		if (input == null) {
			fl.process(new InputStreamSourceStream(charset, CompressionFilter.NONE, System.in, "<<stdin>>"), data);
		}
		else {
			fl.process(input, charset, data);
		}
		return data.values();
	}
	
	private void printAggregatedTable(Collection<List<Aggregator>> aggregatedTable) throws IOException {
		if (output == null) {
			printAggregatedTable(System.out, aggregatedTable);
		}
		else {
			try (PrintStream out = new FileTargetStream(charset, output.getAbsolutePath()).getPrintStream()) {
				printAggregatedTable(out, aggregatedTable);
			}
		}
	}
	
	private void printAggregatedTable(PrintStream out, Collection<List<Aggregator>> aggregatedTable) {
		for (List<Aggregator> line : aggregatedTable) {
			printAggregatedLine(out, line);
		}
	}

	private void printAggregatedLine(PrintStream out, List<Aggregator> line) {
		boolean notFirst = false;
		for (Aggregator aggregator : line) {
			if (notFirst) {
				out.print(outputSeparator);
			}
			else {
				notFirst = true;
			}
			String value = aggregator.get();
			out.print(value);
		}
		out.println();
	}

	@Override
	public String getResourceBundleName() {
		return TableAggregator.class.getCanonicalName() + "Help";
	}

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		List<String> spec = Strings.split(arg, ':', -1);
		String agg = spec.remove(0);
		AggregatorOptions opts = new AggregatorOptions();
		for (String o : spec) {
			opts.parseOption(o);
		}
		specification.add(getAggregatorFactory(agg, opts));
		return false;
	}
	
	private static class AggregatorOptions {
		private boolean lax = true;
		private boolean sorted = false;
		private String separator = ", ";
		private String format = null;
		
		private void parseOption(String opt) {
			if (opt.equals("strict")) {
				lax = false;
			}
			else if (opt.equals("sorted")) {
				sorted = true;
			}
			else if (opt.contains("%")) {
				format = opt;
			}
			else {
				separator = opt;
			}
		}
		
		private String getMeanFormat() {
			if (format == null) {
				return "%f";
			}
			return format;
		}
		
		private String getCountValuesFormat() {
			if (format == null) {
				return "%s (%d)";
			}
			return format;
		}
	}
	
	private static AggregatorFactory getAggregatorFactory(String agg, AggregatorOptions opts) throws CLIOException {
		switch (agg) {
			case "group":
			case "-":
				return GroupBy.INSTANCE;
			case "count":
				return new CountNonEmpty.Factory();
			case "count-empty":
				return new CountEmpty.Factory();
			case "first":
				return new First.Factory();
			case "sum":
				return new Sum.Factory(opts.lax);
			case "min":
				return new Min.Factory(opts.lax);
			case "max":
				return new Max.Factory(opts.lax);
			case "mean":
				return new Mean.Factory(opts.lax, opts.getMeanFormat());
			case "list":
				return new CollectionAggregator.Factory(CollectionFactory.LIST, opts.separator);
			case "set":
				return new CollectionAggregator.Factory(opts.sorted ? CollectionFactory.SORTED_SET : CollectionFactory.SET, opts.separator);
			case "count-values":
				return new CountValues.Factory(opts.getCountValuesFormat(), opts.separator);
		}
		throw new CLIOException("unknown aggregator: " + agg);
	}
	
	@CLIOption("-input")
	public void setInput(File input) {
		this.input = input;
	}
	
	@CLIOption("-output")
	public void setOutput(File output) {
		this.output = output;
	}
	
	@CLIOption("-charset")
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	@CLIOption("-separator")
	public void setSeparator(String separator) {
		format.setSeparator(separator.charAt(0));
		outputSeparator = separator;
	}
	
	@CLIOption("-skip-empty")
	public void skipEmpty() {
		format.setSkipBlank(true);
		format.setSkipEmpty(true);
	}
	
	@CLIOption(value = "-help", stop = true)
	public void help() {
		System.out.print(usage());
		ResourceBundle bundle = ResourceBundle.getBundle(getResourceBundleName(), Locale.getDefault());
		System.out.println("Column specifications:");
		for (String key : new String[] { "group", "count", "count-empty", "first", "sum", "min", "max", "mean", "set", "list", "count-values" }) {
			String opt = bundle.getString(key + ".opt");
			String help = bundle.getString(key + ".help");
			System.out.print("    ");
			System.out.println(opt);
			System.out.print("        ");
			System.out.println(help);
			System.out.println();
		}
	}

	public static void main(String[] args) throws InvalidFileLineEntry, IOException, CLIOException {
		TableAggregator inst = new TableAggregator();
		if (!inst.parse(args)) {
			inst.run();
		}
	}
}
