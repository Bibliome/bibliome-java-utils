package org.bibliome.util.pubmed;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public enum PubMedIndexField {
	PMID("pmid", "/MedlineCitation/PMID") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			String pmid = XMLUtils.evaluateString(xPath, doc);
			addIndexedDataField(luceneDoc, fieldName, pmid);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}
	},
	
	MESH_ID("mesh-id", "/MedlineCitation/MeshHeadingList/MeshHeading/DescriptorName") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			for (Element mesh : XMLUtils.evaluateElements(xPath, doc)) {
				String meshId = mesh.getAttribute(ATTRIBUTE_MESH_ID);
				addIndexedField(luceneDoc, fieldName, meshId);
			}
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}
	},
	
	MESH_PATH("mesh-path", "/MedlineCitation/MeshHeadingList/MeshHeading/DescriptorName") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			for (Element mesh : XMLUtils.evaluateElements(xPath, doc)) {
				String meshId = mesh.getAttribute(ATTRIBUTE_MESH_ID);
				if (meshPaths.containsKey(meshId)) {
					String meshPath = meshPaths.get(meshId);
					addIndexedField(luceneDoc, fieldName, meshPath);
				}
			}
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}
	},

	TITLE("title", "/MedlineCitation/Article/ArticleTitle") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			String title = XMLUtils.evaluateString(xPath, doc);
			addIndexedField(luceneDoc, fieldName, title);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new StandardAnalyzer(PubMedIndexUtils.LUCENE_VERSION);
		}
	},

	ABSTRACT("abstract", "/MedlineCitation/Article/Abstract/AbstractText") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException, DOMException {
			for (Element abs : XMLUtils.evaluateElements(xPath, doc)) {
				String absText = abs.getTextContent();
				addIndexedField(luceneDoc, fieldName, absText);
			}
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new StandardAnalyzer(PubMedIndexUtils.LUCENE_VERSION);
		}
	},

	YEAR("year", "/MedlineCitation/Article/Journal/JournalIssue/PubDate/*[name() = 'Year' or name() = 'MedlineDate']") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			String date = XMLUtils.evaluateString(xPath, doc);
			String year = extractYear(date);
			NumericField yearField = new NumericField(fieldName, Field.Store.NO, true);
			yearField.setIntValue(Integer.parseInt(extractYear(year)));
			luceneDoc.add(yearField);
		}

		private String extractYear(String date) {
			Matcher m = YEAR_PATTERN.matcher(date);
			if (m.find()) {
				return m.group();
			}
			return null;
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}
	},

	JOURNAL("journal", "/MedlineCitation/Article/Journal/Title") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			String journal = XMLUtils.evaluateString(xPath, doc);
			addIndexedField(luceneDoc, fieldName, journal);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new StandardAnalyzer(PubMedIndexUtils.LUCENE_VERSION);
		}
	},
	
	AUTHOR("author", "/MedlineCitation/Article/AuthorList/Author/*[name() = 'ForeName' or name() = 'LastName']") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException, DOMException {
			for (Element author : XMLUtils.evaluateElements(xPath, doc)) {
				String authorName = author.getTextContent();
				addIndexedField(luceneDoc, fieldName, authorName);
			}
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new StandardAnalyzer(PubMedIndexUtils.LUCENE_VERSION, Collections.EMPTY_SET);
		}
	},
	
	SOURCE("source") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			addDataField(luceneDoc, fieldName, source);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}
	},
	
	XML("xml") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) {
			StringWriter sw = new StringWriter();
			XMLUtils.writeDOMToFile(doc, null, sw);
			String xml = sw.toString();
			addDataField(luceneDoc, fieldName, xml);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}
	};

	private static final String ATTRIBUTE_MESH_ID = "UI";
	private static final Pattern YEAR_PATTERN = Pattern.compile("\\d{4}");

	public final String fieldName;
	protected final XPathExpression xPath;
	
	private PubMedIndexField(String fieldName, String xPath) {
		this.fieldName = fieldName;
		try {
			this.xPath = XMLUtils.xp.compile(xPath);
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
	
	private PubMedIndexField(String fieldName) {
		this.fieldName = fieldName;
		this.xPath = null;
	}

	protected abstract void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException;
	
	protected abstract Analyzer getAnalyzer();

	private static void addDataField(org.apache.lucene.document.Document result, String fieldName, String fieldValue) {
		Field field = new Field(fieldName, fieldValue, Field.Store.YES, Field.Index.NO, Field.TermVector.NO);
		result.add(field);
	}

	private static void addIndexedField(org.apache.lucene.document.Document result, String fieldName, String fieldValue) {
		Field field = new Field(fieldName, fieldValue, Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
		result.add(field);
	}

	private static void addIndexedDataField(org.apache.lucene.document.Document result, String fieldName, String fieldValue) {
		Field field = new Field(fieldName, fieldValue, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO);
		result.add(field);
	}
}
