package org.bibliome.util.pubmed;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
	PMID("pmid", "/PubmedArticle/MedlineCitation/PMID") {
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
	
	MESH_ID("mesh-id", "/PubmedArticle/MedlineCitation/MeshHeadingList/MeshHeading/DescriptorName") {
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
	
	MESH_PATH("mesh-path", "/PubmedArticle/MedlineCitation/MeshHeadingList/MeshHeading/DescriptorName") {
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

	TITLE("title", "/PubmedArticle/MedlineCitation/Article/ArticleTitle") {
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

	ABSTRACT("abstract", "/PubmedArticle/MedlineCitation/Article/Abstract/AbstractText") {
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

	YEAR("year", "/PubmedArticle/MedlineCitation/Article/Journal/JournalIssue/PubDate/*[name() = 'Year' or name() = 'MedlineDate']") {
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

	JOURNAL("journal", "/PubmedArticle/MedlineCitation/Article/Journal/Title") {
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
	
	AUTHOR("author", "/PubmedArticle/MedlineCitation/Article/AuthorList/Author/*[name() = 'ForeName' or name() = 'LastName']") {
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
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws TransformerException {
			StringWriter sw = new StringWriter();
			Source xslSource = new DOMSource(doc);
			Result xslResult = new StreamResult(sw);
			Transformer transformer = getTransformer();
			transformer.transform(xslSource, xslResult);
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

	protected abstract void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException, TransformerException;
	
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
	
	private static Transformer getTransformer() throws TransformerConfigurationException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer result = tf.newTransformer();
		result.setOutputProperty(OutputKeys.INDENT, "yes");
		result.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		return result;
	}
}
