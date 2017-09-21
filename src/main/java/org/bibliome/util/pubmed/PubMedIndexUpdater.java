package org.bibliome.util.pubmed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.bibliome.util.Iterators;
import org.bibliome.util.clio.CLIOException;
import org.bibliome.util.clio.CLIOParser;
import org.bibliome.util.clio.CLIOption;
import org.bibliome.util.streams.CollectionSourceStream;
import org.bibliome.util.streams.CompressionFilter;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.StreamFactory;
import org.bibliome.util.xml.XMLUtils;
import org.xml.sax.SAXException;

public class PubMedIndexUpdater extends CLIOParser {
	private final StreamFactory streamFactory = new StreamFactory();
	private File indexDir;
	private final Collection<SourceStream> sources = new ArrayList<SourceStream>();
	private final Map<String,String> meshPaths = new HashMap<String,String>();
	private Pattern filenamePattern = Pattern.compile("medline\\d+n\\d+\\.xml(?:\\.gz)?");
	private boolean checkIndexedFile = true;

	public PubMedIndexUpdater() {
		super();
		streamFactory.setCompressionFilter(CompressionFilter.FILE_EXTENSION);
		streamFactory.setRecursive(true);
	}
	
	@CLIOption(stop=true, value="-help")
	public void help() {
		System.out.println(usage());
	}
	
	@CLIOption("-force")
	public void force() {
		checkIndexedFile = false;
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
			PubMedIndexDOMBuilderHandler handler = new PubMedIndexDOMBuilderHandler(XMLUtils.docBuilder, indexWriter, meshPaths);
			PubmedIndexProperties properties = new PubmedIndexProperties(indexWriter);
			SourceStream source = new CollectionSourceStream("UTF-8", sources);
			for (InputStream is : Iterators.loop(source.getInputStreams())) {
				String streamName = source.getStreamName(is);
				String filename = getFilename(streamName);
				if (shouldParse(properties, filename)) {
					System.err.format("parsing and indexing: %s (%s)\n", filename, streamName);
					handler.resetCounts();
					handler.setSource(filename);
					parser.parse(is, handler);
					properties.addIndexedFile(filename);
					indexWriter.commit();
					System.err.format("  citations updated: %d, deleted: %d\n", handler.getUpdatedCitationsCount(), handler.getDeletedCitationsCount());
				}
				else {
					System.err.println("skipping: " + filename);
				}
			}
			System.err.println("updating index properties");
			properties.update(indexWriter);
			indexWriter.commit();
		}
	}

	private boolean shouldParse(PubmedIndexProperties properties, String filename) {
		if (checkIndexedFile && properties.isIndexedFile(filename)) {
			return false;
		}
		Matcher m = filenamePattern.matcher(filename);
		return m.matches();
	}
	
	private static String getFilename(String streamName) {
		int slash = streamName.lastIndexOf(File.separatorChar);
		if (slash == -1) {
			return streamName;
		}
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
