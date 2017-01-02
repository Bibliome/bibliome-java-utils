package org.bibliome.util.xml;

import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.Attributes;

/**
 * DOM builder handler that triggers the construction of a DOM tree when a start element with a given name is met.
 * @author rbossy
 *
 */
public abstract class TagElementDOMBuilderHandler extends DOMBuilderHandler {
	private final String elementTagName;

	/**
	 * Creates a tag element DOM builder that triggers the construction of a DOM tree when a start element with the specified name is met.
	 * @param docBuilder
	 * @param elementTagName
	 */
	public TagElementDOMBuilderHandler(DocumentBuilder docBuilder, String elementTagName) {
		super(docBuilder);
		this.elementTagName = elementTagName;
	}

	@Override
	protected boolean rootNode(String uri, String localName, String qName, Attributes attributes) {
		return qName.equals(elementTagName);
	}
}
