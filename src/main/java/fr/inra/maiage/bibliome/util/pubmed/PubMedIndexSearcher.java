package fr.inra.maiage.bibliome.util.pubmed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import fr.inra.maiage.bibliome.util.clio.CLIOException;
import fr.inra.maiage.bibliome.util.clio.CLIOParser;
import fr.inra.maiage.bibliome.util.clio.CLIOption;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.StreamFactory;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

public class PubMedIndexSearcher extends CLIOParser {
	private static final String XML_HEADER = "<PubmedArticleSet>";
	private static final String XML_FOOTER = "</PubmedArticleSet>";
	private static final CharSequence BATCH_NUMBER_PLACEHOLDER = "%%";

	private BooleanQuery query = new BooleanQuery();
	private File indexDir = null;
	private int batchSize = Integer.MAX_VALUE;
	private String outputBaseFormat = ".";
	private String pmidOutputFormat = null;
	private String xmlOutputFormat = null;

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		try {
			Analyzer analyzer = PubMedIndexUtils.getGlobalAnalyzer();
			QueryParser parser = new QueryParser(PubMedIndexUtils.LUCENE_VERSION, PubMedIndexField.ABSTRACT.fieldName, analyzer);
			parser.setDefaultOperator(QueryParser.AND_OPERATOR);
			parser.setLowercaseExpandedTerms(false);
			PubMedIndexUtils.log("parsing query: %s", arg);
			Query q = parser.parse(arg);
			PubMedIndexUtils.log("query: %s", q);
			addClause(q);
			return false;
		}
		catch (ParseException e) {
			throw new CLIOException(e);
		}
	}
	
	private void addClause(Query q) {
		query.add(q, Occur.MUST);
	}

	@Override
	public String getResourceBundleName() {
		return PubMedIndexSearcher.class.getCanonicalName() + "Help";
	}
	
	@CLIOption(stop=true, value="-help")
	public void help() {
		System.out.println(usage());
	}

	@CLIOption(stop=true, value="-fields")
	public static void fields() {
		for (PubMedIndexField f : PubMedIndexField.values()) {
			if (f.isIndexed()) {
				System.out.println(f.fieldName);
			}
		}
	}
	
	@CLIOption("-index")
	public void setIndexDir(File indexDir) {
		this.indexDir = indexDir;
	}

	@CLIOption("-pmid-query")
	public void addPMIDList(String location) throws IOException, URISyntaxException {
		addListQuery(location, PubMedIndexField.PMID, QueryFactory.TERM);
	}

	@CLIOption("-doi-query")
	public void addDOIList(String location) throws IOException, URISyntaxException {
		addListQuery(location, PubMedIndexField.DOI, QueryFactory.TERM);
	}

	@CLIOption("-mesh-tree-query")
	public void addMeSHTreeList(String location) throws IOException, URISyntaxException {
		addListQuery(location, PubMedIndexField.MESH_TREE, QueryFactory.PREFIX);
	}
	
	private static enum QueryFactory {
		TERM {
			@Override
			public Query createQuery(Term term) {
				return new TermQuery(term);
			}
		},
		PREFIX {
			@Override
			public Query createQuery(Term term) {
				return new PrefixQuery(term);
			}
		};
		
		public Query createQuery(PubMedIndexField field, String text) {
			return createQuery(new Term(field.fieldName, text));
		}
		
		protected abstract Query createQuery(Term term);
	}

	private void addListQuery(String location, PubMedIndexField field, QueryFactory queryFactory) throws IOException, URISyntaxException {
		BooleanQuery booleanQuery = new BooleanQuery();
		StreamFactory streamFactory = new StreamFactory();
		SourceStream source = streamFactory.getSourceStream(location);
		try (BufferedReader r = source.getBufferedReader()) {
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				Query q = queryFactory.createQuery(field, line.trim());
				booleanQuery.add(q, Occur.SHOULD);
			}
		}
		addClause(booleanQuery);
	}

	@CLIOption("-after")
	public void addRevisionDate(String date) {
		Query q = new TermRangeQuery(PubMedIndexField.DATE_REVISED.fieldName, date, "9", true, false);
		addClause(q);
	}

	@CLIOption("-batch")
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	@CLIOption("-outdir")
	public void setOutputBaseFormat(String outputBaseFormat) {
		this.outputBaseFormat = outputBaseFormat;
	}

	@CLIOption("-pmid")
	public void setPmidOutputFormat(String pmidOutputFormat) {
		this.pmidOutputFormat = pmidOutputFormat;
	}

	@CLIOption("-xml")
	public void setXmlOutputFormat(String xmlOutputFormat) {
		this.xmlOutputFormat = xmlOutputFormat;
	}

	private void search() throws IOException {		
		Directory dir = FSDirectory.open(indexDir);
		try (IndexReader indexReader = IndexReader.open(dir)) {
			try (IndexSearcher indexSearcher = new IndexSearcher(indexReader)) {
				TopDocs topDocs = indexSearcher.search(query, Integer.MAX_VALUE);
				PubMedIndexUtils.log("found %d hits", topDocs.totalHits);
				output(indexReader, topDocs);
				PubMedIndexUtils.log("done");
			}
		}
	}
	
	private void output(IndexReader indexReader, TopDocs topDocs) throws CorruptIndexException, IOException {
		int nBatches = 1 + (topDocs.totalHits / batchSize);
		String batchNumberFormat = getBatchNumberFormat(nBatches);
		String outputBaseFormat = createFormatString(this.outputBaseFormat, batchNumberFormat);
		String pmidOutputFormat = createFormatString(this.pmidOutputFormat, batchNumberFormat);
		String xmlOutputFormat = createFormatString(this.xmlOutputFormat, batchNumberFormat);
		PubMedIndexUtils.log("creating %d batches", nBatches);
		for (int batch = 0; batch < nBatches; ++batch) {
			outputBatch(indexReader, topDocs, batch, outputBaseFormat, pmidOutputFormat, xmlOutputFormat);
		}
	}
	
	private static String createFormatString(String format, String batchNumberFormat) {
		if (format == null) {
			return null;
		}
		return format.replace(BATCH_NUMBER_PLACEHOLDER, batchNumberFormat);
	}
	
	private static String getBatchNumberFormat(int nBatches) {
		double nLog = Math.log10(nBatches);
		int nDigits = Math.max((int) Math.ceil(nLog), 1);
		return String.format("%%0%dd", nDigits);
	}

	private void outputBatch(IndexReader indexReader, TopDocs topDocs, int batch, String outputBaseFormat, String pmidOutputFormat, String xmlOutputFormat) throws CorruptIndexException, IOException {
		String outputBasePath = String.format(outputBaseFormat, batch);
		OutputDirectory outputBaseDir = new OutputDirectory(outputBasePath);
		int start = batch * batchSize;
		int end = Math.min(start + batchSize, topDocs.totalHits);
		if (pmidOutputFormat != null) {
			try (PrintStream out = open(batch, outputBaseDir, pmidOutputFormat)) {
				for (int d = start; d < end; ++d) {
					outputBatchDocument(indexReader, topDocs, out, PubMedIndexField.PMID, d);
				}
			}
		}
		if (xmlOutputFormat != null) {
			try (PrintStream out = open(batch, outputBaseDir, xmlOutputFormat)) {
				out.println(XML_HEADER);
				for (int d = start; d < end; ++d) {
					outputBatchDocument(indexReader, topDocs, out, PubMedIndexField.XML, d);
				}
				out.println(XML_FOOTER);
			}
		}
	}

	private static PrintStream open(int batch, OutputDirectory outputBaseDir, String outputFormat) throws IOException {
		String outputPath = String.format(outputFormat, batch);
		OutputFile outputFile = new OutputFile(outputBaseDir, outputPath);
		TargetStream target = new FileTargetStream("UTF-8", outputFile);
		PubMedIndexUtils.log("writing to %s", outputFile.getAbsolutePath());
		return target.getPrintStream();
	}

	private static void outputBatchDocument(IndexReader indexReader, TopDocs topDocs, PrintStream out, PubMedIndexField pubmedField, int d) throws CorruptIndexException, IOException {
		int docId = topDocs.scoreDocs[d].doc;
		Document doc = indexReader.document(docId);
		Fieldable field = doc.getFieldable(pubmedField.fieldName);
		String value = field.stringValue();
		out.println(value);
	}
	
	public static void main(String[] args) throws CLIOException, IOException {
		PubMedIndexUtils.log("hello");
		PubMedIndexSearcher inst = new PubMedIndexSearcher();
		if (inst.parse(args)) {
			return;
		}
		if (inst.query.clauses().isEmpty()) {
			throw new CLIOException("missing query");
		}
		if (inst.indexDir == null) {
			throw new CLIOException("missing index location");
		}
		inst.search();
	}
}
