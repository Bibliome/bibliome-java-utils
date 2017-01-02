package org.bibliome.util.taxonomy.dict;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bibliome.util.FlushedStreamHandler;
import org.bibliome.util.StandardFormatter;
import org.bibliome.util.clio.CLIOException;
import org.bibliome.util.clio.CLIOParser;
import org.bibliome.util.clio.CLIOption;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.taxonomy.NCBITaxonomy;
import org.bibliome.util.taxonomy.Name;
import org.bibliome.util.taxonomy.Taxon;
import org.bibliome.util.taxonomy.reject.RejectDisjunction;
import org.bibliome.util.taxonomy.reject.RejectName;
import org.bibliome.util.taxonomy.reject.RejectNone;
import org.bibliome.util.taxonomy.saturate.Saturate;

/**
 * Creates taxon dictionaries.
 * @author rbossy
 *
 */
public class BuildDictionary extends CLIOParser {
	private File nodesFile;
	private int size = 1793891;
	private final Collection<File> namesFiles = new ArrayList<File>();
	private File saturationFile;
	private File rejectionFile;
	private final List<TaxonNamePattern> pattern = new ArrayList<TaxonNamePattern>(Arrays.asList(
			TaxonNamePatterns.NAME,
			new ConstantPattern("\t"),
			TaxonNamePatterns.TAXID,
			new ConstantPattern("\n")));
	private String pathSeparator = "/";
	private boolean iterateNames = true;
	
	@Override
	public String getResourceBundleName() {
		return BuildDictionary.class.getCanonicalName() + "Help";
	}

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		if (nodesFile != null)
			throw new CLIOException("Only one nodes file");
		nodesFile = (File) convertArgument(File.class, arg);
		return false;
	}

	/**
	 * Sets the maximum taxon identifier.
	 * @param nodesSize
	 */
	@CLIOption("-nodesSize")
	public void setNodesSize(int nodesSize) {
		size = nodesSize + 1;
	}
	
	/**
	 * Sets the names.dmp file to read.
	 * @param namesFile
	 */
	@CLIOption("-namesFile")
	public void addNamesFile(File namesFile) {
		namesFiles.add(namesFile);
	}
	
	/**
	 * Sets the name saturator file to read.
	 * @param saturationFile
	 */
	@CLIOption("-saturationFile")
	public void setSaturationFile(File saturationFile) {
		this.saturationFile = saturationFile;
	}
	
	/**
	 * Sets the name rejection file to read.
	 * @param rejectionFile
	 */
	@CLIOption("-rejectionFile")
	public void setRejectionFile(File rejectionFile) {
		this.rejectionFile = rejectionFile;
	}
	
	/**
	 * Sets the path separator character.
	 * @param pathSeparator
	 */
	@CLIOption("-pathSeparator")
	public void setPathSeparator(String pathSeparator) {
		this.pathSeparator = pathSeparator;
	}
	
	/**
	 * Sets the dictionary line pattern.
	 * @param pattern
	 */
	@CLIOption("-pattern")
	public void setPattern(String pattern) {
		this.pattern.clear();
		boolean esc = false;
		boolean inCurly = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pattern.length(); ++i) {
			char c = pattern.charAt(i);
			if (inCurly) {
				if (c == '}') {
					this.pattern.add(TaxonNamePatterns.valueOf(sb.toString()));
					inCurly = false;
					sb.setLength(0);
				}
				else
					sb.append(c);
				continue;
			}
			if (esc) {
				sb.append(escapeOf(c));
				esc = false;
				continue;
			}
			if (c == '\\') {
				esc = true;
				continue;
			}
			if (c == '{') {
				if (sb.length() > 0)
					this.pattern.add(new ConstantPattern(sb.toString()));
				sb.setLength(0);
				inCurly = true;
				continue;
			}
			sb.append(c);
		}
		this.pattern.add(new ConstantPattern(sb.toString()));
	}

	/**
	 * Builds a taxon dictionary rather than a name dictionary.
	 */
	@CLIOption(value="-taxaDict")
	public void noIterateNames() {
		iterateNames = false;
	}

	/**
	 * Print help.
	 */
	@CLIOption(value="-help", stop=true)
	public void help() {
		System.out.println(usage());
	}
	
	private static char escapeOf(char c) {
		switch (c) {
		case 'n': return '\n';
		case 'r': return '\r';
		case 't': return '\t';
		}
		return c;
	}
	
	private static Logger getLogger() {
		Logger result = Logger.getAnonymousLogger();
		result.setLevel(Level.ALL);
		result.setUseParentHandlers(false);
		Handler handler = new FlushedStreamHandler(System.err, new StandardFormatter());
		handler.setLevel(Level.ALL);
		result.addHandler(handler);
		return result;
	}

	public static void main(String[] args) throws CLIOException, IOException, InvalidFileLineEntry {
		BuildDictionary inst = new BuildDictionary();
		if (inst.parse(args))
			return;
		Logger logger = getLogger();
		if (!inst.iterateNames) {
			boolean err = false;
			for (TaxonNamePattern pat : inst.pattern)
				if (pat.isNameRequired()) {
					err = true;
					logger.severe("pattern " + pat + " requires name");
				}
			if (err)
				System.exit(1);
		}
		NCBITaxonomy taxonomy = new NCBITaxonomy();
		taxonomy.readNodes(logger, inst.nodesFile, inst.size);
		
		/* Name filter and synonym generation */
		RejectName reject = RejectNone.INSTANCE;
		if (inst.rejectionFile != null)
			reject = new RejectDisjunction(NCBITaxonomy.readReject(logger, inst.rejectionFile));
		for (File f : inst.namesFiles)
			taxonomy.readNames(logger, f, reject);
		if (inst.saturationFile != null)
			for (Saturate sat : NCBITaxonomy.readSaturate(logger, inst.saturationFile))
				taxonomy.saturate(reject, sat);
		
		/* Writes the taxonomy */
		int nTaxa = 0;
		int nNames = 0;
		logger.info("writing dictionary");
		for (Taxon taxon : taxonomy.getTaxa()) {
			nTaxa++;
			if (inst.iterateNames) {
				for (Name name : taxon.getNames()) {
					nNames++;
					for (TaxonNamePattern pat : inst.pattern)
						pat.appendValue(logger, taxon, name, inst.pathSeparator, System.out);
				}
			}
			else {
				for (TaxonNamePattern pat : inst.pattern)
					pat.appendValue(logger, taxon, null, inst.pathSeparator, System.out);
			}
		}
		logger.info("# taxa : " + nTaxa);
		logger.info("# names: " + nNames);
	}
}
