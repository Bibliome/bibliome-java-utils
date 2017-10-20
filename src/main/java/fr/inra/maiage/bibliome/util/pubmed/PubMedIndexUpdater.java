package fr.inra.maiage.bibliome.util.pubmed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.clio.CLIOException;
import fr.inra.maiage.bibliome.util.clio.CLIOParser;
import fr.inra.maiage.bibliome.util.clio.CLIOption;
import fr.inra.maiage.bibliome.util.streams.CollectionSourceStream;
import fr.inra.maiage.bibliome.util.streams.CompressionFilter;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.StreamFactory;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

public class PubMedIndexUpdater extends CLIOParser {
	public static final Pattern PUBMED_FILENAME_PATTERN = Pattern.compile("medline\\d+n\\d+\\.xml(?:\\.gz)?");
	private static final String LOCATION_PUBMED_BASELINE = "ftp://ftp.ncbi.nlm.nih.gov/pubmed/baseline/";
	private static final String LOCATION_PUBMED_UPDATEFILES = "ftp://ftp.ncbi.nlm.nih.gov/pubmed/updatefiles/";
	private static final String LOCATION_PUBMED_OPEN = "";

	private static class PubMedFileFilter implements FileFilter {
		private PubmedIndexProperties properties = null;
		private boolean checkIndexedFile = true;

		@Override
		public boolean accept(File file) {
			String name = getFilename(file.getAbsolutePath());
			Matcher m = PUBMED_FILENAME_PATTERN.matcher(name);
			if (!m.matches()) {
				return false;
			}
			if (checkIndexedFile && properties.isIndexedFile(name)) {
				PubMedIndexUtils.log("skipping: %s", file);
				return false;
			}
			return true;
		}
	}
	
	private final PubMedFileFilter fileFilter = new PubMedFileFilter();
	private final StreamFactory streamFactory = new StreamFactory();
	private File indexDir;
	private final Collection<SourceStream> sources = new ArrayList<SourceStream>();
	private final Map<String,String> meshPaths = new HashMap<String,String>();
	private final Map<String,String> openLicenses = new HashMap<String,String>();

	public PubMedIndexUpdater() {
		super();
		streamFactory.setCompressionFilter(CompressionFilter.FILE_EXTENSION);
		streamFactory.setRecursive(true);
		streamFactory.setFilter(fileFilter);
	}
	
	@CLIOption(stop=true, value="-help")
	public void help() {
		System.out.println(usage());
	}
	
	@CLIOption("-force")
	public void force() {
		fileFilter.checkIndexedFile = false;
	}

	@CLIOption("-index")
	public void setIndexDir(File indexDir) {
		this.indexDir = indexDir;
	}
	
	@CLIOption("-mesh-tree")
	public void addMeSHRoots(String meshTreeLocation) throws IOException, URISyntaxException {
		StreamFactory streamFactory = new StreamFactory();
		streamFactory.setCharset("UTF-16");
		SourceStream source = streamFactory.getSourceStream(meshTreeLocation);
		PubMedIndexUtils.log("reading MeSH descriptor tree from %s", meshTreeLocation);
		boolean isHeaderLine = true;
		try (BufferedReader r = source.getBufferedReader()) {
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				if (isHeaderLine) {
					isHeaderLine = false;
					continue;
				}
				line = line.trim();
				int tab = line.indexOf('\t');
				String meshPath = line.substring(0, tab);
				String rest = line.substring(tab + 1);
				tab = rest.indexOf('\t');
				String meshId = rest.substring(0, tab);
				meshPaths.put(meshId, meshPath);
			}
		}
	}

	@CLIOption("-baseline")
	public void downloadBaseline() throws MalformedURLException, IOException {
		PubMedIndexUtils.log("downloading baseline file list: %s", LOCATION_PUBMED_BASELINE);
		SourceStream source = new PubMedListingSourceStream(LOCATION_PUBMED_BASELINE, fileFilter);
		sources.add(source);
	}

	@CLIOption("-update-files")
	public void downloadUpdateFiles() throws MalformedURLException, IOException {
		PubMedIndexUtils.log("downloading update file list: %s", LOCATION_PUBMED_UPDATEFILES);
		SourceStream source = new PubMedListingSourceStream(LOCATION_PUBMED_UPDATEFILES, fileFilter);
		sources.add(source);
	}

	@CLIOption("-open-access")
	public void indexOpenAccessStatus() throws IOException, URISyntaxException {
		PubMedIndexUtils.log("downloading open access list: %s", LOCATION_PUBMED_OPEN);
		SourceStream source = streamFactory.getSourceStream(LOCATION_PUBMED_OPEN);
		try (BufferedReader r = source.getBufferedReader()) {
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				List<String> cols = Strings.split(line, '\t', -1);
				String pmid = cols.get(5);
				if (pmid.isEmpty()) {
					continue;
				}
				if (!pmid.startsWith("PMID:")) {
					continue;
				}
				pmid = pmid.substring(5);
				String license = cols.get(6);
				openLicenses.put(pmid, license);
			}
		}
	}

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		try {
			SourceStream stream = streamFactory.getSourceStream(arg);
			sources.add(stream);
			return false;
		}
		catch (IOException|URISyntaxException e) {
			throw new CLIOException(e);
		}
	}

	@Override
	public String getResourceBundleName() {
		return PubMedIndexUpdater.class.getCanonicalName() + "Help";
	}

	public void update() throws CorruptIndexException, IOException, ParserConfigurationException, SAXException {
		try (IndexWriter indexWriter = openIndexWriter(indexDir)) {
			SAXParser parser = createParser();
			PubMedIndexDOMBuilderHandler handler = new PubMedIndexDOMBuilderHandler(XMLUtils.docBuilder, indexWriter, meshPaths, openLicenses);
			PubmedIndexProperties properties = new PubmedIndexProperties(indexWriter);
			fileFilter.properties = properties;
			SourceStream source = new CollectionSourceStream("UTF-8", sources);
			for (InputStream is : Iterators.loop(source.getInputStreams())) {
				String streamName = source.getStreamName(is);
				String filename = getFilename(streamName);
				PubMedIndexUtils.log("parsing and indexing: %s", filename);
				handler.resetCounts();
				handler.setSource(filename);
				parser.parse(is, handler);
				properties.addIndexedFile(filename);
				properties.update(indexWriter);
				indexWriter.commit();
				PubMedIndexUtils.log("citations updated: %d", handler.getUpdatedCitationsCount());
				PubMedIndexUtils.log("citations deleted: %d", handler.getDeletedCitationsCount());
			}
		}
	}

	private static String getFilename(String streamName) {
		int slash = streamName.lastIndexOf(File.separatorChar);
		String filename = streamName.substring(slash + 1);
		return filename.replace(".gz", "");
	}
	
	private static SAXParser createParser() throws ParserConfigurationException, SAXException {
		SAXParserFactory pf = SAXParserFactory.newInstance();
		return pf.newSAXParser();
	}

	private static IndexWriterConfig getIndexWriterConfig() {
		Analyzer analyzer = PubMedIndexUtils.getGlobalAnalyzer();
		IndexWriterConfig result = new IndexWriterConfig(PubMedIndexUtils.LUCENE_VERSION, analyzer);
		result.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		return result;
	}
	
	private static IndexWriter openIndexWriter(File indexPath) throws IOException {
		Directory dir = FSDirectory.open(indexPath);
		IndexWriterConfig config = getIndexWriterConfig();
		return new IndexWriter(dir, config);
	}
	
	public static void main(String[] args) throws CLIOException, CorruptIndexException, IOException, ParserConfigurationException, SAXException {
		PubMedIndexUpdater inst = new PubMedIndexUpdater();
		if (inst.parse(args)) {
			return;
		}
		if (inst.indexDir == null) {
			throw new CLIOException("missing index location");
		}
		if (inst.sources.isEmpty()) {
			throw new CLIOException("missing source files location");
		}
		inst.update();
	}
}
