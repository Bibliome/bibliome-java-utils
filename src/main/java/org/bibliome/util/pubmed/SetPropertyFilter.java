package org.bibliome.util.pubmed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.bibliome.util.filters.Filter;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class SetPropertyFilter implements Filter<Document> {
	private final XPathExpression expression;
	private final Set<String> include;
	
	protected SetPropertyFilter(XPathExpression expression, Set<String> include) {
		super();
		this.expression = expression;
		this.include = include;
	}

	@Override
	public boolean accept(Document x) {
		try {
			for (Element e : XMLUtils.evaluateElements(expression, x)) {
				String s = getValue(e);
				if (include.contains(s)) {
					return true;
				}
			}
			return false;
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract String getValue(Element e);

	protected static BufferedReader openVerbose(File f) throws FileNotFoundException {
		System.err.format("Loading %s...\n", f.getAbsolutePath());
		Reader r = new FileReader(f);
		return new BufferedReader(r);
	}
	
	protected static Set<String> loadIDs(File idFile) throws IOException {
		Set<String> result = new HashSet<String>();
		try (BufferedReader br = openVerbose(idFile)) {
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (!line.isEmpty()) {
					result.add(line);
				}
			}
		}
		return result;
	}
}
