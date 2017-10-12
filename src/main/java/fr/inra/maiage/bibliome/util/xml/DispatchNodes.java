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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import fr.inra.maiage.bibliome.util.defaultmap.DefaultArrayListHashMap;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultMap;

public class DispatchNodes {
	private final DefaultMap<String,List<Element>> elements = new DefaultArrayListHashMap<String,Element>();
	private final List<Text> textNodes = new ArrayList<Text>();
	private final boolean ignoreBlankText;

	public DispatchNodes(boolean ignoreBlankText) {
		super();
		this.ignoreBlankText = ignoreBlankText;
	}

	public DispatchNodes() {
		this(true);
	}

	public Map<String,List<Element>> getElements() {
		return Collections.unmodifiableMap(elements);
	}

	public List<Text> getTextNodes() {
		return Collections.unmodifiableList(textNodes);
	}

	public boolean isIgnoreBlankText() {
		return ignoreBlankText;
	}
	
	public boolean hasText() {
		return !textNodes.isEmpty();
	}
	
	public boolean hasElements() {
		return !elements.isEmpty();
	}
	
	public boolean hasElements(String tag) {
		return elements.containsKey(tag);
	}
	
	public Collection<String> getTags() {
		return Collections.unmodifiableCollection(elements.keySet());
	}
	
	public List<Element> consumeElements(String tag) {
		List<Element> result = elements.safeGet(tag, false);
		elements.remove(tag);
		return result;
	}

	public void dispatchChildren(Node node) {
		for (Node child : XMLUtils.childrenNodes(node)) {
			dispatchNode(child);
		}
	}

	public void dispatchNode(Node node) {
		if (node instanceof Element) {
			addElement((Element) node);
		}
		else if (node instanceof Text) {
			addText((Text) node);
		}
	}

	private void addText(Text text) {
		if (ignoreBlankText) {
			String contents = text.getNodeValue();
			contents = contents.trim();
			if (contents.isEmpty()) {
				return;
			}
		}
		textNodes.add(text);
	}

	private void addElement(Element elt) {
		String name = elt.getTagName();
		List<Element> list = elements.safeGet(name);
		list.add(elt);
	}
}
