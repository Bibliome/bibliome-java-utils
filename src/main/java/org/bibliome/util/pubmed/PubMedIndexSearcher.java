package org.bibliome.util.pubmed;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;

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
import org.bibliome.util.files.OutputDirectory;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.TargetStream;

public class PubMedIndexSearcher extends CLIOParser {
	private static final String XML_HEADER = null;
	private static final String XML_FOOTER = null;

	private String queryString;
	private File indexDir;
	private int batchSize = Integer.MAX_VALUE;
	private MessageFormat outputBaseFormat;
	private MessageFormat pmidOutputFormat;
	private MessageFormat xmlOutputFormat;

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		queryString = arg;
		return true;
	}

	@Override
	public String getResourceBundleName() {
		// TODO Auto-generated method stub
		return null;
	}
		
	private void search() throws ParseException, IOException {
		Analyzer analyzer = PubMedIndexUtils.getGlobalAnalyzer();
		QueryParser parser = new QueryParser(PubMedIndexUtils.LUCENE_VERSION, PubMedIndexField.ABSTRACT.fieldName, analyzer);
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query query = parser.parse(queryString);
		
		Directory dir = FSDirectory.open(indexDir);
		try (IndexReader indexReader = IndexReader.open(dir)) {
			try (IndexSearcher indexSearcher = new IndexSearcher(indexReader)) {
				TopDocs topDocs = indexSearcher.search(query, Integer.MAX_VALUE);
				output(indexReader, topDocs);
			}
		}
	}
	
	private void output(IndexReader indexReader, TopDocs topDocs) throws CorruptIndexException, IOException {
		int nBatches = 1 + (topDocs.totalHits / batchSize);
		for (int batch = 0; batch < nBatches; ++batch) {
			outputBatch(indexReader, topDocs, batch);
		}
	}

	private void outputBatch(IndexReader indexReader, TopDocs topDocs, int batch) throws CorruptIndexException, IOException {
		String outputBasePath = outputBaseFormat.format(batch);
		OutputDirectory outputBaseDir = new OutputDirectory(outputBasePath);
		int start = batch * batchSize;
		int end = start + Math.min(start + batchSize, topDocs.totalHits);
		if (xmlOutputFormat != null) {
			try (PrintStream out = open(batch, outputBaseDir, xmlOutputFormat)) {
				out.println(XML_HEADER);
				for (int d = start; d < end; ++d) {
					outputBatchDocument(indexReader, topDocs, out, PubMedIndexField.XML, d);
				}
				out.println(XML_FOOTER);
			}
		}
		if (pmidOutputFormat != null) {
			try (PrintStream out = open(batch, outputBaseDir, pmidOutputFormat)) {
				for (int d = start; d < end; ++d) {
					outputBatchDocument(indexReader, topDocs, out, PubMedIndexField.PMID, d);
				}
			}
		}
	}

	private static PrintStream open(int batch, OutputDirectory outputBaseDir, MessageFormat outputFormat) throws IOException {
		String outputPath = outputFormat.format(batch);
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
}
