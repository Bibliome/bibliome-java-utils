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

package fr.inra.maiage.bibliome.util.taxonomy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.util.filelines.FileLines;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.filelines.TabularFormat;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.taxonomy.reject.RejectConjunction;
import fr.inra.maiage.bibliome.util.taxonomy.reject.RejectName;
import fr.inra.maiage.bibliome.util.taxonomy.reject.RejectNamePattern;
import fr.inra.maiage.bibliome.util.taxonomy.reject.RejectNone;
import fr.inra.maiage.bibliome.util.taxonomy.reject.RejectTaxid;
import fr.inra.maiage.bibliome.util.taxonomy.saturate.Saturate;
import fr.inra.maiage.bibliome.util.taxonomy.saturate.SaturatePattern;

/**
 * NCBI taxonomy.
 * @author rbossy
 *
 */
public class NCBITaxonomy {
	private final String idPrefix;
	private final Map<String,Taxon> taxa = new HashMap<String,Taxon>();
	
	public NCBITaxonomy(String idPrefix) {
		super();
		this.idPrefix = idPrefix;
	}

	private String buildTaxid(String rawId) {
		if (rawId.indexOf(':') == -1) {
			return String.format("%s%s", idPrefix, rawId);
		}
		return rawId;
	}

	private final class NodesFileLines extends DmpFileLines<Map<String,String>> {
		private final StringCache rankCache = new StringCache();
		
		private NodesFileLines() {
			super(14);
		}

		@Override
		public void processEntry(Map<String,String> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			String id = buildTaxid(entry.get(0));
			//data[id] = Integer.parseInt(entry.get(1));
			Taxon taxon = new Taxon(id, rankCache.get(entry.get(2)), Integer.parseInt(entry.get(4)));
			addTaxon(taxon);
			String parentId = buildTaxid(entry.get(1));
			data.put(id, parentId);
		}
	}
	
	private void addTaxon(Taxon taxon) {
		String id = taxon.getTaxid();
		if (taxa.containsKey(id))
			throw new IllegalArgumentException("duplicate taxid: " + id);
		taxa.put(id, taxon);
	}
	
	/**
	 * Reads a NCBI nodes.dmp file.
	 * @param logger
	 * @param file
	 * @param size
	 * @throws IOException
	 * @throws InvalidFileLineEntry
	 */
	public void readNodes(Logger logger, File file) throws IOException, InvalidFileLineEntry {
		logger.info("reading nodes file: " + file.getCanonicalPath());
		NodesFileLines fl = new NodesFileLines();
		fl.setLogger(logger);
		Map<String,String> parent = new HashMap<String,String>();
		fl.process(file, DmpFileLines.CHARSET, parent);
		//System.err.println(parent);
		for (Map.Entry<String,String> e : parent.entrySet()) {
			String taxid = e.getKey();
			String parentId = e.getValue();
			Taxon taxon = taxa.get(taxid);
			Taxon p = taxa.get(parentId);
			if (taxon.getParent() != null) {
				throw new RuntimeException("already a parent " + taxid + ": " + parentId + " / " + taxon.getParent().getTaxid());
			}
			if (!taxid.equals(parentId)) {
				taxon.setParent(p);
			}
		}
	}
	
	private final class NamesFileLines extends DmpFileLines<NCBITaxonomy> {
		private final StringCache typeCache = new StringCache();
		private final RejectName reject;
		
		private NamesFileLines() {
			this(RejectNone.INSTANCE);
		}
		
		private NamesFileLines(RejectName reject) {
			super(5);
			this.reject = reject;
		}

		@Override
		public void processEntry(NCBITaxonomy data, int lineno, List<String> entry)	throws InvalidFileLineEntry {
			String id = buildTaxid(entry.get(0));
			if (!data.taxa.containsKey(id)) {
				throw new IllegalArgumentException("unknown taxid: " + id);
			}
			Taxon taxon = data.taxa.get(id);
			taxon.addName(reject, entry.get(1), typeCache.get(entry.get(3)));
		}
	}
	
	/**
	 * Reads a NCBI names.dmp file.
	 * @param logger
	 * @param file
	 * @throws IOException
	 * @throws InvalidFileLineEntry
	 */
	public void readNames(Logger logger, File file) throws IOException, InvalidFileLineEntry {
		logger.info("reading names file: " + file.getCanonicalPath());
		NamesFileLines fl = new NamesFileLines();
		fl.setLogger(logger);
		fl.process(file, DmpFileLines.CHARSET, this);
	}

	public void readNames(Logger logger, File file, RejectName reject) throws IOException, InvalidFileLineEntry {
		logger.info("reading names file: " + file.getCanonicalPath());
		NamesFileLines fl = new NamesFileLines(reject);
		fl.setLogger(logger);
		fl.process(file, DmpFileLines.CHARSET, this);
	}

	/**
	 * Removes all names rejected by the specified name reject.
	 * @param reject
	 */
	public void reject(RejectName reject) {
		for (Taxon taxon : taxa.values())
			taxon.reject(reject);
	}

	/**
	 * Adds all names according to the specified saturator.
	 * @param saturate
	 */
	public void saturate(Saturate saturate) {
		for (Taxon taxon : taxa.values())
			taxon.saturate(saturate);
	}
	
	public void saturate(RejectName reject, Saturate saturate) {
		for (Taxon taxon : taxa.values())
			taxon.saturate(reject, saturate);
	}

	/**
	 * Reads a name reject file.
	 * @param logger
	 * @param file
	 * @throws IOException
	 */
	public Collection<RejectName> readReject(Logger logger, File file, Collection<RejectName> result) throws IOException {
		logger.info("reading rejection file: " + file.getCanonicalPath());
		InputFile inputFile = new InputFile(file.getCanonicalPath());
		SourceStream source = new FileSourceStream(DmpFileLines.CHARSET, inputFile);
		BufferedReader r = source.getBufferedReader();
		while (true) {
			String line = r.readLine();
			if (line == null) {
				break;
			}
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}
			if (line.charAt(0) == '#') {
				continue;
			}
			result.add(getReject(line));
		}
		return result;
	}

	public Collection<RejectName> readReject(Logger logger, File file) throws IOException {
		return readReject(logger, file, new ArrayList<RejectName>());
	}
	
	private static final Pattern TAXID_WITH_PREFIX = Pattern.compile("[A-Z_a-z]+:\\d+");
	
	private RejectName getReject(String line) {
		int tab = line.indexOf('\t');
		if (tab >= 0) {
			return new RejectConjunction(getReject(line.substring(0, tab)), getReject(line.substring(tab+1)));
		}
		Matcher m = TAXID_WITH_PREFIX.matcher(line);
		if (m.matches()) {
			return new RejectTaxid(line);
		}
		return new RejectNamePattern(Pattern.compile(line));
	}
	
	private static class SaturateFileLines extends FileLines<Collection<Saturate>> {
		private SaturateFileLines() {
			super();
			TabularFormat format = getFormat();
			format.setSeparator('\t');
			format.setMinColumns(3);
			format.setMaxColumns(Integer.MAX_VALUE);
			format.setTrimColumns(true);
			format.setSkipBlank(true);
			format.setSkipEmpty(true);
		}

		@Override
		public void processEntry(Collection<Saturate> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			Collection<MessageFormat> formats = new ArrayList<MessageFormat>(entry.size() - 2);
			for (int i = 2; i < entry.size(); ++i)
				formats.add(new MessageFormat(entry.get(i)));
			data.add(new SaturatePattern(Pattern.compile(entry.get(0)), entry.get(1), formats));
		}
	}
	
	/**
	 * Reads a name saturator file.
	 * @param logger
	 * @param file
	 * @throws IOException
	 * @throws InvalidFileLineEntry
	 */
	public static Collection<Saturate> readSaturate(Logger logger, File file) throws IOException, InvalidFileLineEntry {
		logger.info("reading saturation file: " + file.getCanonicalPath());
		SaturateFileLines fl = new SaturateFileLines();
		Collection<Saturate> result = new ArrayList<Saturate>();
		fl.process(file, DmpFileLines.CHARSET, result);
		return result;
	}
	
	/**
	 * Returns all taxa in this taxonomy.
	 */
	public Collection<Taxon> getTaxa() {
		return Collections.unmodifiableCollection(taxa.values());
	}
	
	public Taxon getTaxon(String taxId) {
		return taxa.get(taxId);
	}
}
