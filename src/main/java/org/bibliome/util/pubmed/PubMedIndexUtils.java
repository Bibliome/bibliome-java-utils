package org.bibliome.util.pubmed;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public enum PubMedIndexUtils {
	;

	public static final String ATTRIBUTE_MESH_ID = "UI";
	public static final Version LUCENE_VERSION = Version.LUCENE_36;

	public static Analyzer getGlobalAnalyzer() {
		Map<String,Analyzer> fieldAnalyzers = new HashMap<String,Analyzer>();
		for (PubMedIndexField f : PubMedIndexField.values()) {
			fieldAnalyzers.put(f.fieldName, f.getAnalyzer());
		}
		return new PerFieldAnalyzerWrapper(new KeywordAnalyzer(), fieldAnalyzers);
	}

	public static IndexWriterConfig getIndexWriterConfig() {
		Analyzer analyzer = getGlobalAnalyzer();
		return new IndexWriterConfig(LUCENE_VERSION, analyzer);
	}
	
	public static IndexWriter openIndexWriter(File indexPath) throws IOException {
		Directory dir = FSDirectory.open(indexPath);
		IndexWriterConfig config = getIndexWriterConfig();
		return new IndexWriter(dir, config);
	}
}
