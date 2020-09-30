package fr.inra.maiage.bibliome.util.pubmed;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

public class PubmedIndexProperties {
	private static final String GLOBAL_PROPERTIES_FIELD = "__global-properties";
	private static final String GLOBAL_PROPERTIES_VALUE = "__global-properties";
	private static final Term GLOBAL_PROPERTIES_TERM = new Term(GLOBAL_PROPERTIES_FIELD, GLOBAL_PROPERTIES_VALUE);
	private static final String INDEXED_FILE_FIELD = "__indexed-file";

	private final Document doc;
	private Collection<String> indexedFiles;
	
	public PubmedIndexProperties(IndexSearcher indexSearcher) throws IOException {
		Query query = new TermQuery(GLOBAL_PROPERTIES_TERM);
		TopDocs topDocs = indexSearcher.search(query, 1);
		if (topDocs.totalHits.value < 1) {
			this.doc = new Document();
			Field globalPropertiesField = new StringField(GLOBAL_PROPERTIES_FIELD, GLOBAL_PROPERTIES_VALUE, Field.Store.YES);
			this.doc.add(globalPropertiesField);
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
		this(DirectoryReader.open(indexWriter));
	}
	
	private void ensureIndxedFiles() {
		if (indexedFiles != null) {
			return;
		}
		indexedFiles = new LinkedHashSet<String>();
		for (IndexableField f : doc.getFields(INDEXED_FILE_FIELD)) {
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
		Field indexedFileField = new StoredField(INDEXED_FILE_FIELD, fileName);
		doc.add(indexedFileField);
	}
	
	public void update(IndexWriter indexWriter) throws CorruptIndexException, IOException {
		indexWriter.updateDocument(GLOBAL_PROPERTIES_TERM, doc);
	}

	public Collection<String> getIndexedFiles() {
		ensureIndxedFiles();
		return Collections.unmodifiableCollection(indexedFiles);
	}
}
