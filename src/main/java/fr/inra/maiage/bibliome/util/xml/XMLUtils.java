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

package fr.inra.maiage.bibliome.util.xml;

import java.io.File;
import java.io.Writer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import fr.inra.maiage.bibliome.util.Strings;

/**
 * Miscellanous XML utilities.
 * 
 * @author rbossy
 */
public class XMLUtils {
    private static final DocumentBuilderFactory       docBuilderFactory = DocumentBuilderFactory.newInstance();

    /** A DocumentBuilder to create DOM document. */
    public static final DocumentBuilder              docBuilder;

    private static final XPathFactory                 xpFactory         = XPathFactory.newInstance();

    public static final XPath                        xp                = xpFactory.newXPath();

    /** XSLT stylesheet factory */
    public static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    /** xpath: "." */
    public static final XPathExpression CONTENTS;
    
    static {
        try {
        	CONTENTS = xp.compile(".");
            docBuilder = docBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException pce) {
            throw new Error(pce);
        }
        catch (XPathExpressionException xpee) {
            throw new Error(xpee);
        }
    }

    private static final class NodeListIterator implements Iterator<Node> {
        private final NodeList nodeList;
        private int            i = 0;

        private NodeListIterator(NodeList nodeList) {
            super();
            this.nodeList = nodeList;
        }

        @Override
        public boolean hasNext() {
            return i < nodeList.getLength();
        }

        @Override
        public Node next() {
            if (hasNext())
                return nodeList.item(i++);
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class NodeListList extends AbstractList<Node> {
        private final NodeList nodeList;

        private NodeListList(NodeList nodeList) {
            super();
            this.nodeList = nodeList;
        }

        @Override
        public Node get(int index) {
            try {
                return nodeList.item(index);
            }
            catch (IndexOutOfBoundsException e) {
                return null;
            }
        }

        @Override
        public int size() {
            return nodeList.getLength();
        }

        @Override
        public Iterator<Node> iterator() {
            return new NodeListIterator(nodeList);
        }
    }

    /**
     * NodeList wrapper that implements the standard List interface.
     * @param nodeList
     */
    public static final List<Node> getListOfNodes(NodeList nodeList) {
        if (nodeList == null)
            return Collections.emptyList();
        return new NodeListList(nodeList);
    }

    /**
     * Returns all elements in the specified list.
     * @param nodes
     */
    public static final List<Element> elementList(List<Node> nodes) {
        List<Element> result = new ArrayList<Element>();
        for (Node node : nodes)
            if (node instanceof Element)
                result.add((Element)node);
        return result;
    }

    /**
     * Returns all elements in the specified list.
     * @param nodes
     */
    public static final List<Element> elementList(NodeList nodes) {
        return elementList(new NodeListList(nodes));
    }

    /**
     * Returns all children nodes as a list.
     * @param node
     */
    public static final List<Node> childrenNodes(Node node) {
        return new NodeListList(node.getChildNodes());
    }

    /**
     * Returns all children elements as a list.
     * @param node
     */
    public static final List<Element> childrenElements(Node node) {
        return elementList(node.getChildNodes());
    }

    /**
     * Evaluate an XPath expression as a String.
     * @param xpe
     * @param item
     * @throws XPathExpressionException
     */
    public static final String evaluateString(XPathExpression xpe, Object item) throws XPathExpressionException {
        return (String)xpe.evaluate(item, XPathConstants.STRING);
    }

    /**
     * Evaluate an XPath expression as a String.
     * @param expr
     * @param item
     * @throws XPathExpressionException
     */
    public static final String evaluateString(String expr, Object item) throws XPathExpressionException {
        return evaluateString(xp.compile(expr), item);
    }

    /**
     * Evaluate an XPath expression as a node list.
     * @param xpe
     * @param item
     * @throws XPathExpressionException
     */
    public static final List<Node> evaluateNodes(XPathExpression xpe, Object item) throws XPathExpressionException {
        return getListOfNodes((NodeList)xpe.evaluate(item, XPathConstants.NODESET));
    }

    /**
     * Evaluate an XPath expression as a node list.
     * @param expr
     * @param item
     * @throws XPathExpressionException
     */
    public static final List<Node> evaluateNodes(String expr, Object item) throws XPathExpressionException {
        return evaluateNodes(xp.compile(expr), item);
    }

    /**
     * Evaluate an XPath expression as a list of elements.
     * @param xpe
     * @param item
     * @throws XPathExpressionException
     */
    public static final List<Element> evaluateElements(XPathExpression xpe, Object item) throws XPathExpressionException {
        return elementList((NodeList)xpe.evaluate(item, XPathConstants.NODESET));
    }

    /**
     * Evaluate an XPath expression as a list of elements.
     * @param expr
     * @param item
     * @throws XPathExpressionException
     */
    public static final List<Element> evaluateElements(String expr, Object item) throws XPathExpressionException {
        return evaluateElements(xp.compile(expr), item);
    }

    /**
     * Evaluate an XPath expression as a single node.
     * @param xpe
     * @param item
     * @throws XPathExpressionException
     */
    public static final Node evaluateNode(XPathExpression xpe, Object item) throws XPathExpressionException {
        return (Node)xpe.evaluate(item, XPathConstants.NODE);
    }

    /**
     * Evaluate an XPath expression as a single node.
     * @param expr
     * @param item
     * @throws XPathExpressionException
     */
    public static final Node evaluateNode(String expr, Object item) throws XPathExpressionException {
        return evaluateNode(xp.compile(expr), item);
    }

    /**
     * Evaluate an XPath expression as a single element.
     * @param xpe
     * @param item
     * @throws XPathExpressionException
     */
    public static final Element evaluateElement(XPathExpression xpe, Object item) throws XPathExpressionException {
        return (Element)xpe.evaluate(item, XPathConstants.NODE);
    }

    /**
     * Evaluate an XPath expression as a single element.
     * @param expr
     * @param item
     * @throws XPathExpressionException
     */
    public static final Element evaluateElement(String expr, Object item) throws XPathExpressionException {
        return evaluateElement(xp.compile(expr), item);
    }

    /**
     * Returns the value of one of the attributes, if none is present the text contents of the specified node.
     * @param element
     * @param alternateAttributes
     */
    public static final String attributeOrValue(Element element, String mainAttribute, String... alternateAttributes) {
        if (element.hasAttribute(mainAttribute))
            return element.getAttribute(mainAttribute);
        if (alternateAttributes != null) {
            for (String a : alternateAttributes)
                if (element.hasAttribute(a))
                    return element.getAttribute(a);
        }
        return element.getTextContent();
    }
    
    /**
     * Write DOM to file.
     * @param node
     * @param result
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    public static final void writeDOMToFile(Node node, String doctypeSystem, Result result) {
        try {
			Source source = new DOMSource(node);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			if (doctypeSystem != null) {
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctypeSystem);
			}
			transformer.setOutputProperty(OutputKeys.INDENT, "true");
			transformer.transform(source, result);
		}
		catch (TransformerFactoryConfigurationError|TransformerException e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Write DOM to file.
     * @param node
     * @param file
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    public static final void writeDOMToFile(Node node, String doctypeSystem, File file) {
        writeDOMToFile(node, doctypeSystem, new StreamResult(file));
    }

    /**
     * Write DOM to file.
     * @param node
     * @param writer
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    public static final void writeDOMToFile(Node node, String doctypeSystem, Writer writer) {
        writeDOMToFile(node, doctypeSystem, new StreamResult(writer));
    }

    /**
     * Write DOM to file.
     * @param node
     * @param path
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    public static final void writeDOMToFile(Node node, String doctypeSystem, String path) {
        writeDOMToFile(node, doctypeSystem, new File(path));
    }

    /**
     * Returns a boolean value from the specified element attribute.
     * @param element
     * @param attribute
     * @param defaultValue
     */
    public static final boolean getBooleanAttribute(Element element, String attribute, boolean defaultValue) {
        if (element.hasAttribute(attribute))
            return Strings.getBoolean(element.getAttribute(attribute));
        return defaultValue;
    }

    /**
     * Returns the attribute value, or defaultValue if the specified element does not have the attribute.
     * @param element
     * @param attribute
     * @param defaultValue
     */
    public static final String getAttribute(Element element, String attribute, String defaultValue) {
    	if (element.hasAttribute(attribute))
    		return element.getAttribute(attribute);
    	return defaultValue;
    }
    
    /**
     * Returns the attribute value converted as an integer, or defaultValue if the specified element does not have the attribute.
     * @param element
     * @param attribute
     * @param defaultValue
     */
    public static final int getIntegerAttribute(Element element, String attribute, int defaultValue) {
    	if (element.hasAttribute(attribute))
    		return Strings.getInteger(element.getAttribute(attribute), defaultValue);
    	return defaultValue;
    }
    
    public static final double getDoubleAttribute(Element element, String attribute, double defaultValue) {
    	if (element.hasAttribute(attribute))
    		return Strings.getDouble(element.getAttribute(attribute), defaultValue);
    	return defaultValue;
    }
    
    /**
     * Creates a root element for the specified document.
     * @param doc
     * @param name
     */
	public static Element createRootElement(Document doc, String name) {
		Element result = doc.createElement(name);
		doc.appendChild(result);
		return result;
	}
	
	/**
	 * Creates an element in the specified document.
	 * This method also creates indentation text nodes.
	 * @param doc
	 * @param parent
	 * @param depth
	 * @param name
	 */
	public static Element createElement(Document doc, Node parent, int depth, String name) {
		Element result = doc.createElement(name);
		if (depth >= 0)
			createIndent(doc, parent, depth);
		if (parent != null)
			parent.appendChild(result);
		if (depth >= 0)
			createIndent(doc, parent, depth - 1);
		return result;
	}

	/**
	 * Creates a text node in the specified document.
	 * @param doc
	 * @param parent
	 * @param contents
	 */
	public static Text createText(Document doc, Node parent, String contents) {
		Text result = doc.createTextNode(contents);
		if (parent != null)
			parent.appendChild(result);
		return result;
	}
	
	private static Text createIndent(Document doc, Node parent, int depth) {
		StringBuilder sb = new StringBuilder(System.getProperty("line.separator"));
		for (int i = 0; i < depth; ++i)
			sb.append("  ");
		return createText(doc, parent, sb.toString());
	}

	/**
	 * Creates an element in the specified document, the created element has a single text node child with the specified contents.
	 * @param doc
	 * @param parent
	 * @param depth
	 * @param name
	 * @param contents
	 */
	public static Element createElement(Document doc, Node parent, int depth, String name, String contents) {
		Element result = createElement(doc, parent, depth, name);
		createText(doc, result, contents);
		return result;
	}
	
	private static class ListNodeList implements NodeList {
		private final List<? extends Node> list;

		private ListNodeList(List<? extends Node> list) {
			super();
			this.list = list;
		}

		@Override
		public int getLength() {
			return list.size();
		}

		@Override
		public Node item(int index) {
			if (index < 0)
				return null;
			if (index >= list.size())
				return null;
			return list.get(0);
		}
	}

	/**
	 * Returns the specified list of nodes as a NodeList.
	 * @param list
	 */
	public static NodeList getNodeList(List<? extends Node> list) {
		return new ListNodeList(list);
	}
	
	public static void ignoreDTD(SAXParserFactory spf) throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
		spf.setFeature("http://xml.org/sax/features/validation", false);
		spf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	}
}
