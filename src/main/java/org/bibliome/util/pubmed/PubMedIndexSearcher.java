package org.bibliome.util.pubmed;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.bibliome.util.clio.CLIOException;
import org.bibliome.util.clio.CLIOParser;
import org.bibliome.util.clio.CLIOption;
import org.bibliome.util.files.OutputDirectory;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.TargetStream;

public class PubMedIndexSearcher extends CLIOParser {
	private static final String XML_HEADER = "<PubmedArticleSet>";
	private static final String XML_FOOTER = "</PubmedArticleSet>";
	private static final CharSequence BATCH_NUMBER_PLACEHOLDER = "%%";

	private String queryString;
	private File indexDir;
	private int batchSize = Integer.MAX_VALUE;
	private String outputBaseFormat = ".";
	private String pmidOutputFormat = null;
	private String xmlOutputFormat = null;

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		queryString = arg;
		return false;
	}

	@Override
	public String getResourceBundleName() {
		// TODO Auto-generated method stub
		return null;
	}

	@CLIOption("-index")
	public void setIndexDir(File indexDir) {
		this.indexDir = indexDir;
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

	private void search() throws ParseException, IOException {
		Analyzer analyzer = PubMedIndexUtils.getGlobalAnalyzer();
		QueryParser parser = new QueryParser(PubMedIndexUtils.LUCENE_VERSION, PubMedIndexField.ABSTRACT.fieldName, analyzer);
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		System.err.println("parsing query: " + queryString);
		Query query = parser.parse(queryString);
		
		Directory dir = FSDirectory.open(indexDir);
		try (IndexReader indexReader = IndexReader.open(dir)) {
			try (IndexSearcher indexSearcher = new IndexSearcher(indexReader)) {
				TopDocs topDocs = indexSearcher.search(query, Integer.MAX_VALUE);
				System.err.format("found %d hits\n", topDocs.totalHits);
				output(indexReader, topDocs);
			}
		}
	}
	
	private void output(IndexReader indexReader, TopDocs topDocs) throws CorruptIndexException, IOException {
		int nBatches = 1 + (topDocs.totalHits / batchSize);
		String batchNumberFormat = getBatchNumberFormat(nBatches);
		String outputBaseFormat = createFormatString(this.outputBaseFormat, batchNumberFormat);
		String pmidOutputFormat = createFormatString(this.pmidOutputFormat, batchNumberFormat);
		String xmlOutputFormat = createFormatString(this.xmlOutputFormat, batchNumberFormat);
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
		int nDigits = (int) Math.ceil(nLog);
		return String.format("%%0%dd", nDigits);
	}

	private void outputBatch(IndexReader indexReader, TopDocs topDocs, int batch, String outputBaseFormat, String pmidOutputFormat, String xmlOutputFormat) throws CorruptIndexException, IOException {
		String outputBasePath = String.format(outputBaseFormat, batch);
		OutputDirectory outputBaseDir = new OutputDirectory(outputBasePath);
		int start = batch * batchSize;
		int end = start + Math.min(start + batchSize, topDocs.totalHits);
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
		return target.getPrintStream();
	}

	private static void outputBatchDocument(IndexReader indexReader, TopDocs topDocs, PrintStream out, PubMedIndexField pubmedField, int d) throws CorruptIndexException, IOException {
		int docId = topDocs.scoreDocs[d].doc;
		Document doc = indexReader.document(docId);
		Fieldable field = doc.getFieldable(pubmedField.fieldName);
		String value = field.stringValue();
		out.println(value);
	}
	
	public static void main(String[] args) throws CLIOException, ParseException, IOException {
		PubMedIndexSearcher inst = new PubMedIndexSearcher();
		if (inst.parse(args)) {
			return;
		}
		if (inst.queryString == null) {
			throw new CLIOException("missing query");
		}
		if (inst.indexDir == null) {
			throw new CLIOException("missing index location");
		}
		inst.search();
	}
}
