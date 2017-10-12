package fr.inra.maiage.bibliome.util.pubmed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	public static void log(String msg) {
		Date date = new Date();
		System.err.println("["+DATE_FORMAT.format(date)+"] "+msg);
	}
	
	public static void log(String fmt, Object obj) {
		Date date = new Date();
		System.err.format("["+DATE_FORMAT.format(date)+"] "+fmt+"\n", obj);
	}
}
