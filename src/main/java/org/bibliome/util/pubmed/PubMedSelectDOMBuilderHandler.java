package org.bibliome.util.pubmed;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.bibliome.util.PatternFilenameFilter;
import org.bibliome.util.filters.Filter;
import org.bibliome.util.xml.TagElementDOMBuilderHandler;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class PubMedSelectDOMBuilderHandler extends TagElementDOMBuilderHandler {
	private final Document targetDocument;
	private final Element pubmedArticleSet;
	private final Filter<Document> filter;
	private final XPathExpression pmidXPath;
	private final Set<String> pmids = new HashSet<String>();

	public PubMedSelectDOMBuilderHandler(Filter<Document> filter) throws XPathExpressionException {
		super(XMLUtils.docBuilder, "PubmedArticle");
		targetDocument = XMLUtils.docBuilder.newDocument();
		pubmedArticleSet = targetDocument.createElement("PubmedArticleSet");
		targetDocument.appendChild(pubmedArticleSet);
		pubmedArticleSet.appendChild(targetDocument.createTextNode("\n"));
		this.filter = filter;
		this.pmidXPath = XMLUtils.xp.compile("/PubmedArticle/MedlineCitation/PMID");
	}

	@Override
	protected void handleDOMTree(Document doc) throws Exception {
		if (filter.accept(doc)) {
			String pmid = XMLUtils.evaluateString(pmidXPath, doc);
			pmids.add(pmid);
			Element article = doc.getDocumentElement();
			targetDocument.adoptNode(article);
			pubmedArticleSet.appendChild(targetDocument.createTextNode("  "));
			pubmedArticleSet.appendChild(article);
			pubmedArticleSet.appendChild(targetDocument.createTextNode("\n"));
		}
	}

	public Document getTargetDocument() {
		return targetDocument;
	}
	
	public Set<String> getPMIDs() {
		return pmids;
	}

	public void select(SAXParser saxParser, File source) throws SAXException, IOException {
		if (source.isDirectory()) {
			FilenameFilter xmlFilenames = new PatternFilenameFilter(Pattern.compile("\\.xml$"));
			for (File f : source.listFiles(xmlFilenames)) {
				select(saxParser, f);
			}
		}
		else {
			System.err.format("Select articles from %s...\n", source.getAbsolutePath());
		    saxParser.parse(source, this);
		}
	}
	
	public void select(File source) throws SAXException, IOException, ParserConfigurationException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		XMLUtils.ignoreDTD(spf);
		SAXParser saxParser = spf.newSAXParser();
		select(saxParser, source);
	}
}
