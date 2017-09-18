package org.bibliome.util.pubmed;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

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
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, Map<String,String> meshPaths) throws XPathExpressionException {
			String pmid = XMLUtils.evaluateString(xPath, doc);
			addField(luceneDoc, fieldName, pmid);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}
	},
	
	MESH_ID("mesh-id", "/MedlineCitation/MeshHeadingList/MeshHeading/DescriptorName") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, Map<String,String> meshPaths) throws XPathExpressionException {
			for (Element mesh : XMLUtils.evaluateElements(xPath, doc)) {
				String meshId = mesh.getAttribute(PubMedIndexUtils.ATTRIBUTE_MESH_ID);
				addField(luceneDoc, fieldName, meshId);
			}
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}
	},
	
	MESH_PATH("mesh-path", "/MedlineCitation/MeshHeadingList/MeshHeading/DescriptorName") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, Map<String,String> meshPaths) throws XPathExpressionException {
			for (Element mesh : XMLUtils.evaluateElements(xPath, doc)) {
				String meshId = mesh.getAttribute(PubMedIndexUtils.ATTRIBUTE_MESH_ID);
				if (meshPaths.containsKey(meshId)) {
					String meshPath = meshPaths.get(meshId);
					addField(luceneDoc, fieldName, meshPath);
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
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, Map<String,String> meshPaths) throws XPathExpressionException {
			String title = XMLUtils.evaluateString(xPath, doc);
			addField(luceneDoc, fieldName, title);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new StandardAnalyzer(PubMedIndexUtils.LUCENE_VERSION);
		}
	},

	ABSTRACT("abstract", "/MedlineCitation/Article/Abstract/AbstractText") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, Map<String,String> meshPaths) throws XPathExpressionException, DOMException {
			for (Element abs : XMLUtils.evaluateElements(xPath, doc)) {
				String absText = abs.getTextContent();
				addField(luceneDoc, fieldName, absText);
			}
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new StandardAnalyzer(PubMedIndexUtils.LUCENE_VERSION);
		}
	},

	YEAR("year", "/MedlineCitation/Article/Journal/PubDate/Year") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, Map<String,String> meshPaths) throws XPathExpressionException {
			String year = XMLUtils.evaluateString(xPath, doc);
			NumericField yearField = new NumericField(fieldName, Field.Store.NO, true);
			yearField.setIntValue(Integer.parseInt(year));
			luceneDoc.add(yearField);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}
	},

	JOURNAL("journal", "/MedlineCitation/Article/Journal/Title") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, Map<String,String> meshPaths) throws XPathExpressionException {
			String journal = XMLUtils.evaluateString(xPath, doc);
			addField(luceneDoc, fieldName, journal);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new StandardAnalyzer(PubMedIndexUtils.LUCENE_VERSION);
		}
	},
	
	AUTHOR("author", "/MedlineCitation/Article/AuthorList/Author/(ForeName|LastName)") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, Map<String,String> meshPaths) throws XPathExpressionException, DOMException {
			for (Element author : XMLUtils.evaluateElements(xPath, doc)) {
				String authorName = author.getTextContent();
				addField(luceneDoc, fieldName, authorName);
			}
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new StandardAnalyzer(PubMedIndexUtils.LUCENE_VERSION, Collections.EMPTY_SET);
		}
	},
	
	XML("xml") {
		@Override
		protected void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, Map<String,String> meshPaths) {
			StringWriter sw = new StringWriter();
			XMLUtils.writeDOMToFile(doc, null, sw);
			String xml = sw.toString();
			addField(luceneDoc, fieldName, xml);
		}

		@Override
		protected Analyzer getAnalyzer() {
			return new KeywordAnalyzer();
		}
	};

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

	protected abstract void addFields(org.apache.lucene.document.Document luceneDoc, Document doc, Map<String,String> meshPaths) throws XPathExpressionException;
	
	protected abstract Analyzer getAnalyzer();

	private static void addField(org.apache.lucene.document.Document result, String fieldName, String fieldValue) {
		Field field = new Field(fieldName, new StringReader(fieldValue));
		result.add(field);
	}
}
