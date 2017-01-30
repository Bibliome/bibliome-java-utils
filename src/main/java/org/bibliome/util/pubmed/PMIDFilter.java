package org.bibliome.util.pubmed;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

public class PMIDFilter extends SetPropertyFilter {
	public PMIDFilter(Set<String> include) throws XPathExpressionException {
		super(XMLUtils.xp.compile("/PubmedArticle/MedlineCitation/PMID"), include);
	}

	public PMIDFilter(File pmidFile) throws XPathExpressionException, IOException {
		this(loadIDs(pmidFile));
	}

	@Override
	protected String getValue(Element e) {
		return e.getTextContent();
	}
}
