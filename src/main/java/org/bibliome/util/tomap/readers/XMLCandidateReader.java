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

package org.bibliome.util.tomap.readers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.util.defaultmap.DefaultArrayListHashMap;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.tomap.Candidate;
import org.bibliome.util.tomap.StringNormalization;
import org.bibliome.util.tomap.Token;
import org.bibliome.util.tomap.TokenNormalization;
import org.bibliome.util.tomap.readers.AbstractReader.ReaderResult;
import org.bibliome.util.tomap.readers.XMLCandidateReader.XMLResult;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class XMLCandidateReader extends AbstractReader<XMLResult> {
	public XMLCandidateReader(Logger logger, TokenNormalization tokenNormalization, StringNormalization stringNormalization) {
		super(logger, tokenNormalization, stringNormalization);
	}

	public static class XMLResult extends ReaderResult {
		private final Collection<Candidate> candidates = new ArrayList<Candidate>();
		private final DefaultMap<Candidate,List<String>> conceptIDs = new DefaultArrayListHashMap<Candidate,String>();
		
		public Collection<Candidate> getCandidates() {
			return Collections.unmodifiableCollection(candidates);
		}
		
		public Map<Candidate,List<String>> getConceptIDs() {
			return Collections.unmodifiableMap(conceptIDs);
		}
	}
	
	public XMLResult fromDOM(Element elt) {
		XMLResult result = new XMLResult();
		for (Element child : XMLUtils.childrenElements(elt)) {
			Candidate candidate = candidateFromDOM(result, child);
			result.candidates.add(candidate);
		}
		return result;
	}
	
	public XMLResult fromDOM(Document doc) {
		return fromDOM(doc.getDocumentElement());
	}
	
	@Override
	public XMLResult parseFile(File file) throws SAXException, IOException {
		Document doc = XMLUtils.docBuilder.parse(file);
		return fromDOM(doc);
	}
	
	@Override
	public XMLResult parseStream(InputStream is) throws SAXException, IOException {
		Document doc = XMLUtils.docBuilder.parse(is);
		return fromDOM(doc);
	}
	
	private Candidate candidateFromDOM(XMLResult xmlResult, Element elt) {
		List<Token> tokens = new ArrayList<Token>();
		Candidate result = new Candidate(tokens);
		for (Element child : XMLUtils.childrenElements(elt)) {
			String name = child.getTagName();
			switch (name) {
				case "t": {
					Token t = tokenFromDOM(child);
					tokens.add(t);
					break;
				}
				case "ht": {
					Token t = tokenFromDOM(child);
					result.setHeadToken(t);
					break;
				}
				case "head": {
					Candidate head = candidateFromDOM(xmlResult, child);
					result.setHead(head);
					break;
				}
				case "mod": {
					Candidate mod = candidateFromDOM(xmlResult, child);
					result.addModifier(mod);
					break;
				}
				case "concept": {
					List<String> conceptIDs = xmlResult.conceptIDs.safeGet(result);
					conceptIDs.add(child.getTextContent());
					break;
				}
				default:
					throw new RuntimeException();
			}
		}
		return result;
	}
	
	private Token tokenFromDOM(Element elt) {
		String form = elt.getTextContent();
		String lemma = XMLUtils.getAttribute(elt, "lemma", null);
		String pos = XMLUtils.getAttribute(elt, "pos", null);
		return getToken(form, lemma, pos);
	}
}
