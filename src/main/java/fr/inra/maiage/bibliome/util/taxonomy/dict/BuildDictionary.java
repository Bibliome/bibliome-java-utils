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

package fr.inra.maiage.bibliome.util.taxonomy.dict;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.util.FlushedStreamHandler;
import fr.inra.maiage.bibliome.util.StandardFormatter;
import fr.inra.maiage.bibliome.util.clio.CLIOException;
import fr.inra.maiage.bibliome.util.clio.CLIOParser;
import fr.inra.maiage.bibliome.util.clio.CLIOption;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.taxonomy.NCBITaxonomy;
import fr.inra.maiage.bibliome.util.taxonomy.Name;
import fr.inra.maiage.bibliome.util.taxonomy.Taxon;
import fr.inra.maiage.bibliome.util.taxonomy.reject.RejectDisjunction;
import fr.inra.maiage.bibliome.util.taxonomy.reject.RejectName;
import fr.inra.maiage.bibliome.util.taxonomy.reject.RejectNone;
import fr.inra.maiage.bibliome.util.taxonomy.saturate.Saturate;

/**
 * Creates taxon dictionaries.
 * @author rbossy
 *
 */
public class BuildDictionary extends CLIOParser {
	private File nodesFile;
	private int size = 2000000;
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
		ResourceBundle bundle = ResourceBundle.getBundle(getResourceBundleName(), Locale.getDefault());
		System.out.println("Pattern elements:");
		for (TaxonNamePattern pat : TaxonNamePatterns.values()) {
			System.out.print("    ");
			String patName = pat.toString();
			System.out.print(patName);
			for (int i = 25 - patName.length(); i > 0; --i) {
				System.out.print(' ');
			}
			String descr = bundle.getString(patName);
			System.out.println(descr);
		}
		System.out.println();
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
