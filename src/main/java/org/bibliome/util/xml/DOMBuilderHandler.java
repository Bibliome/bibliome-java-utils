/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.bibliome.util.xml;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler that build DOM trees corresponding to part of the read document.
 * @author rbossy
 *
 */
public abstract class DOMBuilderHandler extends DefaultHandler {
	private final StringBuilder sb = new StringBuilder();
	private final DocumentBuilder docBuilder;
	private Document doc = null;
	private Node current = null;
	
	/**
	 * Creates a DOM builder handler using the specified document builder to crete DOM nodes.
	 * @param docBuilder
	 */
	public DOMBuilderHandler(DocumentBuilder docBuilder) {
		super();
		this.docBuilder = docBuilder;
	}

	@Override
	public void characters(char[] c, int off, int len) throws SAXException {
		if (doc == null)
			return;
		sb.append(c, off, len);
	}
	
	@Override
	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		if (doc == null)
			return;
		if (sb.length() > 0) {
			Text text = doc.createTextNode(sb.toString());
			current.appendChild(text);
			sb.setLength(0);
		}
		current = current.getParentNode();
		if (current == doc) {
			try {
				handleDOMTree(doc);
			} catch (Exception e) {
				throw new SAXException(e.getMessage(), e);
			}
			doc = null;
			current = null;
		}
	}

	/**
	 * Handles a completed DOM tree.
	 * @param doc
	 * @throws Exception
	 */
	protected abstract void handleDOMTree(Document doc) throws Exception;

	@Override
	public void startDocument() throws SAXException {
		doc = null;
		current = null;
		sb.setLength(0);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (doc != null) {
			current = createElement(current, qName, attributes);
			return;
		}
		if (rootNode(uri, localName, qName, attributes)) {
			doc = docBuilder.newDocument();
			current = createElement(doc, qName, attributes);
		}
	}
	
	/**
	 * Returns either an element start should trigger the construction of a DOM tree.
	 * @param uri
	 * @param localName
	 * @param qName
	 * @param attributes
	 */
	protected abstract boolean rootNode(String uri, String localName, String qName, Attributes attributes);
	
	private Element createElement(Node parent, String qName, Attributes attributes) {
		Element result = doc.createElement(qName);
		for (int i = 0; i < attributes.getLength(); ++i)
			result.setAttribute(attributes.getQName(i), attributes.getValue(i));
		parent.appendChild(result);
		return result;
	}
}
