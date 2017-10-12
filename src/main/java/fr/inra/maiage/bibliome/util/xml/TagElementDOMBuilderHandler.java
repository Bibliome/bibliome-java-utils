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
