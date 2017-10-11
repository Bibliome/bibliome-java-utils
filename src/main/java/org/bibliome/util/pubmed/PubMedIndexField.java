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
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public enum PubMedIndexField {
	PMID("pmid", "/PubmedArticle/MedlineCitation/PMID") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			String pmid = XMLUtils.evaluateString(xPath, doc);
			addField(luceneDoc, pmid);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		public boolean isStored() {
			return true;
		}
	},
	
	DOI("doi", "/PubmedArticle/PubmedData/ArticleIdList/ArticleId[@IdType = 'doi']") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String, String> meshPaths) throws XPathExpressionException, TransformerException {
			String doi = XMLUtils.evaluateString(xPath, doc);
			addField(luceneDoc, doi);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		public boolean isStored() {
			return true;
		}
	},
	
	MESH_ID("mesh-id", "/PubmedArticle/MedlineCitation/MeshHeadingList/MeshHeading/DescriptorName") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			for (Element mesh : XMLUtils.evaluateElements(xPath, doc)) {
				String meshId = mesh.getAttribute(ATTRIBUTE_MESH_ID);
				addField(luceneDoc, meshId);
			}
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		public boolean isStored() {
			return false;
		}
	},
	
	MESH_TREE("mesh-tree", "/PubmedArticle/MedlineCitation/MeshHeadingList/MeshHeading/DescriptorName") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			for (Element mesh : XMLUtils.evaluateElements(xPath, doc)) {
				String meshId = mesh.getAttribute(ATTRIBUTE_MESH_ID);
				if (meshPaths.containsKey(meshId)) {
					String meshPath = meshPaths.get(meshId);
					addField(luceneDoc, meshPath);
				}
			}
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		public boolean isStored() {
			return false;
		}
	},

	TITLE("title", "/PubmedArticle/MedlineCitation/Article/ArticleTitle") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			String title = XMLUtils.evaluateString(xPath, doc);
			addField(luceneDoc, title);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new StandardAnalyzer(PubMedIndexUtils.LUCENE_VERSION);
		}

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		public boolean isStored() {
			return false;
		}
	},

	ABSTRACT("abstract", "/PubmedArticle/MedlineCitation/Article/Abstract/AbstractText") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException, DOMException {
			for (Element abs : XMLUtils.evaluateElements(xPath, doc)) {
				String absText = abs.getTextContent();
				addField(luceneDoc, absText);
			}
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new StandardAnalyzer(PubMedIndexUtils.LUCENE_VERSION);
		}

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		public boolean isStored() {
			return false;
		}
	},

	YEAR("year", "/PubmedArticle/MedlineCitation/Article/Journal/JournalIssue/PubDate/*[name() = 'Year' or name() = 'MedlineDate']") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			String date = XMLUtils.evaluateString(xPath, doc);
			String year = extractYear(date);
			addField(luceneDoc, year);
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

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		public boolean isStored() {
			return false;
		}
	},

	JOURNAL("journal", "/PubmedArticle/MedlineCitation/Article/Journal/Title") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			String journal = XMLUtils.evaluateString(xPath, doc);
			addField(luceneDoc, journal);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new StandardAnalyzer(PubMedIndexUtils.LUCENE_VERSION);
		}

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		public boolean isStored() {
			return false;
		}
	},
	
	AUTHOR("author", "/PubmedArticle/MedlineCitation/Article/AuthorList/Author/*[name() = 'ForeName' or name() = 'LastName']") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException, DOMException {
			for (Element author : XMLUtils.evaluateElements(xPath, doc)) {
				String authorName = author.getTextContent();
				addField(luceneDoc, authorName);
			}
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new StandardAnalyzer(PubMedIndexUtils.LUCENE_VERSION, Collections.EMPTY_SET);
		}

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		public boolean isStored() {
			return false;
		}
	},

	DATE_REVISED("date-revised", "/PubmedArticle/MedlineCitation/DateRevised") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException, TransformerException {
			Element dateRevised = XMLUtils.evaluateElement(xPath, doc);
			String fieldValue = getFieldValue(dateRevised);
			addField(luceneDoc, fieldValue);
		}
		
		private String getFieldValue(Element dateRevised) {
			if (dateRevised == null) {
				return null;
			}
			String year = null;
			String month = null;
			String day = null;
			for (Element child : XMLUtils.childrenElements(dateRevised)) {
				String tagName = child.getTagName();
				String tagContent = child.getTextContent();
				switch (tagName) {
					case "Year": {
						year = tagContent;
						break;
					}
					case "Month": {
						month = tagContent;
						break;
					}
					case "Day": {
						day = tagContent;
						break;
					}
				}
			}
			if (year == null) {
				return null;
			}
			if (month == null) {
				return year;
			}
			if (day == null) {
				return year + "-" + month;
			}
			return year + "-" + month + "-" + day;
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		public boolean isStored() {
			return false;
		}
	},
	
	SOURCE("source") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, String source, Map<String,String> meshPaths) throws XPathExpressionException {
			addField(luceneDoc, source);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}

		@Override
		public boolean isIndexed() {
			return false;
		}

		@Override
		public boolean isStored() {
			return true;
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
			addField(luceneDoc, xml);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}

		@Override
		public boolean isIndexed() {
			return false;
		}

		@Override
		public boolean isStored() {
			return true;
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
	
	public abstract boolean isIndexed();
	
	public abstract boolean isStored();
	
	protected void addField(org.apache.lucene.document.Document doc, String fieldValue) {
		if (fieldValue == null || fieldValue.isEmpty()) {
			return;
		}
		Field.Store store = isStored() ? Field.Store.YES : Field.Store.NO;
		Field.Index index = isIndexed() ? Field.Index.ANALYZED : Field.Index.NO;
		Field field = new Field(fieldName, fieldValue, store, index, Field.TermVector.NO);
		doc.add(field);
	}
	
	private static Transformer getTransformer() throws TransformerConfigurationException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer result = tf.newTransformer();
		result.setOutputProperty(OutputKeys.INDENT, "yes");
		result.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		return result;
	}
}
