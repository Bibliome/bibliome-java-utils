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
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.bibliome.util.Iterators;
import org.bibliome.util.clio.CLIOException;
import org.bibliome.util.clio.CLIOParser;
import org.bibliome.util.clio.CLIOption;
import org.bibliome.util.streams.CollectionSourceStream;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.StreamFactory;
import org.bibliome.util.xml.XMLUtils;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PubMedIndexUpdater extends CLIOParser {
	private final StreamFactory streamFactory = new StreamFactory();
	private File indexDir;
	private final Collection<SourceStream> sources = new ArrayList<SourceStream>();
	private final Map<String,String> meshPaths = new HashMap<String,String>();
	private Pattern filenamePattern = Pattern.compile("medline\\d+\\.xml");
	private final Logger logger;

	public PubMedIndexUpdater(Logger logger) {
		super();
		this.logger = logger;
	}

	@CLIOption("-dir")
	public void setIndexDir(File indexDir) {
		this.indexDir = indexDir;
	}
	
	@CLIOption("-mesh-roots")
	public void addMeSHRoots(String meshRoots) throws IOException, URISyntaxException {
		SourceStream source = streamFactory.getSourceStream(meshRoots);
		try (BufferedReader r = source.getBufferedReader()) {
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				int tab = line.lastIndexOf('\t');
				if (tab != -1) {
					String meshId = line.substring(0, tab);
					String path = line.substring(tab + 1);
					meshPaths.put(meshId, path);
				}
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
		// TODO Auto-generated method stub
		return null;
	}

	public void update() throws CorruptIndexException, IOException, ParserConfigurationException, SAXException {
		try (IndexWriter indexWriter = PubMedIndexUtils.openIndexWriter(indexDir)) {
			SAXParser parser = createParser();
			DefaultHandler handler = new PubMedIndexDOMBuilderHandler(logger, XMLUtils.docBuilder, indexWriter, meshPaths);
			PubmedIndexProperties properties = new PubmedIndexProperties(indexWriter);
			SourceStream source = new CollectionSourceStream("UTF-8", sources);
			for (InputStream is : Iterators.loop(source.getInputStreams())) {
				String streamName = source.getStreamName(is);
				String filename = getFilename(streamName);
				if (shouldParse(properties, filename)) {
					logger.info("parsing and indexing: " + filename);
					parser.parse(is, handler);
					properties.addIndexedFile(filename);
				}
				else {
					logger.info("skipping: " + filename);
				}
				indexWriter.commit();
			}
			logger.info("updating index properties");
			properties.update(indexWriter);
			indexWriter.commit();
		}
	}

	private boolean shouldParse(PubmedIndexProperties properties, String filename) {
		if (properties.isIndexedFile(filename)) {
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
		return streamName.substring(slash + 1);
	}
	
	private static SAXParser createParser() throws ParserConfigurationException, SAXException {
		SAXParserFactory pf = SAXParserFactory.newInstance();
		return pf.newSAXParser();
	}
	
	public static void main(String[] args) throws CLIOException, CorruptIndexException, IOException, ParserConfigurationException, SAXException {
		Logger logger = Logger.getLogger("pubmed-index-updater");
		PubMedIndexUpdater inst = new PubMedIndexUpdater(logger);
		if (inst.parse(args)) {
			return;
		}
		if (inst.sources.isEmpty()) {
			logger.severe("missing source files location");
			System.exit(1);
		}
		inst.update();
	}
}
