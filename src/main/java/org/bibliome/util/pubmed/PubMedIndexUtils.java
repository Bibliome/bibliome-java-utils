package org.bibliome.util.pubmed;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.util.Version;

public enum PubMedIndexUtils {
	;

	public static final Version LUCENE_VERSION = Version.LUCENE_36;

	public static Analyzer getGlobalAnalyzer() {
		Map<String,Analyzer> fieldAnalyzers = new HashMap<String,Analyzer>();
		for (PubMedIndexField f : PubMedIndexField.values()) {
			fieldAnalyzers.put(f.fieldName, f.getAnalyzer());
		}
		return new PerFieldAnalyzerWrapper(new KeywordAnalyzer(), fieldAnalyzers);
	}
}
