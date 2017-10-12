package fr.inra.maiage.bibliome.util.pubmed;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.util.xml.DOMBuilderHandler;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

public class PubMedIndexDOMBuilderHandler extends DOMBuilderHandler {
	public static final String TAG_CITATION = "PubmedArticle";
	public static final String TAG_DELETE = "DeleteCitation";

	private final IndexWriter indexWriter;
	private final Map<String,String> meshPaths;
	private String source = "";
	private int updatedCitationsCount = 0;
	private int deletedCitationsCount = 0;
	
	public PubMedIndexDOMBuilderHandler(DocumentBuilder docBuilder, IndexWriter indexWriter, Map<String,String> meshPaths) {
		super(docBuilder);
		this.indexWriter = indexWriter;
		this.meshPaths = meshPaths;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getUpdatedCitationsCount() {
		return updatedCitationsCount;
	}
	
	public int getDeletedCitationsCount() {
		return deletedCitationsCount;
	}

	public void resetCounts() {
		updatedCitationsCount = 0;
		deletedCitationsCount = 0;
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
			deletedCitationsCount++;
			Term term = new Term(PubMedIndexField.PMID.fieldName, pmid);
			indexWriter.deleteDocuments(term);
		}
	}

	private void updateCitation(Document doc) throws XPathExpressionException, CorruptIndexException, IOException, TransformerException {
		updatedCitationsCount++;
		String pmid = XMLUtils.evaluateString(PubMedIndexField.PMID.xPath, doc);
		org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();
		for (PubMedIndexField field : PubMedIndexField.values()) {
			field.addFields(luceneDoc, doc, source, meshPaths);
		}
		Term term = new Term(PubMedIndexField.PMID.fieldName, pmid);
		indexWriter.updateDocument(term, luceneDoc);
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return new InputSource(new StringReader(""));
	}
}
