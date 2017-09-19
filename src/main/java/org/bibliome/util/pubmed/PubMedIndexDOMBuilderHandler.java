package org.bibliome.util.pubmed;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathExpressionException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.bibliome.util.xml.DOMBuilderHandler;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class PubMedIndexDOMBuilderHandler extends DOMBuilderHandler {
	public static final String TAG_CITATION = "MedlineCitation";
	public static final String TAG_DELETE = "DeleteCitation";

	private final Logger logger;
	private final IndexWriter indexWriter;
	private final Map<String,String> meshPaths;
	
	public PubMedIndexDOMBuilderHandler(Logger logger, DocumentBuilder docBuilder, IndexWriter indexWriter, Map<String,String> meshPaths) {
		super(docBuilder);
		this.logger = logger;
		this.indexWriter = indexWriter;
		this.meshPaths = meshPaths;
	}

	@Override
	protected boolean rootNode(String uri, String localName, String qName, Attributes attributes) {
		return qName.equals(TAG_CITATION) || qName.equals(TAG_DELETE);
	}

	@Override
	protected void handleDOMTree(Document doc) throws Exception {
		Element root = doc.getDocumentElement();
		String rootTag = root.getTagName();
		switch (rootTag) {
			case TAG_CITATION: {
				updateCitation(doc);
				break;
			}
			case TAG_DELETE: {
				deleteCitations(root);
				break;
			}
			default: {
				throw new RuntimeException();
			}
		}
	}
	
	private void deleteCitations(Element root) throws CorruptIndexException, IOException {
		for (Element e : XMLUtils.childrenElements(root)) {
			String pmid = e.getTextContent();
			logger.info("  deleting PMID: " + pmid);
			Term term = new Term(PubMedIndexField.PMID.fieldName, pmid);
			indexWriter.deleteDocuments(term);
		}
	}

	private void updateCitation(Document doc) throws XPathExpressionException, CorruptIndexException, IOException {
		String pmid = XMLUtils.evaluateString(PubMedIndexField.PMID.xPath, doc);
		logger.fine("  updating PMID: " + pmid);
		org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();
		for (PubMedIndexField field : PubMedIndexField.values()) {
			field.addFields(luceneDoc, doc, meshPaths);
		}
		Term term = new Term(PubMedIndexField.PMID.fieldName, pmid);
		indexWriter.updateDocument(term, luceneDoc);
	}
}
