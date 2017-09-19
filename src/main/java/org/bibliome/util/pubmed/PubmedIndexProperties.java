package org.bibliome.util.pubmed;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

public class PubmedIndexProperties {
	private static final String GLOBAL_PROPERTIES_FIELD = "__indexed-file";
	private static final String GLOBAL_PROPERTIES_VALUE = "__indexed-file";
	private static final Term GLOBAL_PROPERTIES_TERM = new Term(GLOBAL_PROPERTIES_FIELD, GLOBAL_PROPERTIES_VALUE);
	private static final String INDEXED_FILE_FIELD = "__indexed-file";

	private final Document doc;
	private Collection<String> indexedFiles;
	
	public PubmedIndexProperties(IndexSearcher indexSearcher) throws IOException {
		Query query = new TermQuery(GLOBAL_PROPERTIES_TERM);
		TopDocs topDocs = indexSearcher.search(query, 1);
		if (topDocs.totalHits < 1) {
			this.doc = new Document();
		}
		else {
			int docId = topDocs.scoreDocs[0].doc;
			this.doc = indexSearcher.doc(docId);
		}
	}
	
	public PubmedIndexProperties(IndexReader indexReader) throws IOException {
		this(new IndexSearcher(indexReader));
	}
	
	public PubmedIndexProperties(IndexWriter indexWriter) throws CorruptIndexException, IOException {
		this(IndexReader.open(indexWriter, true));
	}
	
	private void ensureIndxedFiles() {
		if (indexedFiles != null) {
			return;
		}
		indexedFiles = new LinkedHashSet<String>();
		for (Fieldable f : doc.getFieldables(INDEXED_FILE_FIELD)) {
			String v = f.stringValue();
			indexedFiles.add(v);
		}
	}
	
	public boolean isIndexedFile(String fileName) {
		ensureIndxedFiles();
		return indexedFiles.contains(fileName);
	}
	
	public void addIndexedFile(String fileName) {
		ensureIndxedFiles();
		indexedFiles.add(fileName);
		Field indexedFileField = new Field(INDEXED_FILE_FIELD, fileName, Field.Store.YES, Field.Index.NO);
		doc.add(indexedFileField);
	}
	
	public void update(IndexWriter indexWriter) throws CorruptIndexException, IOException {
		indexWriter.updateDocument(GLOBAL_PROPERTIES_TERM, doc);
	}
}
