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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathExpressionException;

import org.bibliome.util.Iterators;
import org.bibliome.util.Pair;
import org.bibliome.util.defaultmap.DefaultArrayListHashMap;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.tomap.Candidate;
import org.bibliome.util.tomap.StringNormalization;
import org.bibliome.util.tomap.Token;
import org.bibliome.util.tomap.TokenNormalization;
import org.bibliome.util.tomap.readers.AbstractReader.ReaderResult;
import org.bibliome.util.tomap.readers.YateaCandidateReader.YateaResult;
import org.bibliome.util.xml.XMLUtils;
import org.bibliome.util.yatea.AbstractYateaHandler;
import org.bibliome.util.yatea.ModifierPosition;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class YateaCandidateReader extends AbstractReader<YateaResult> {
	private static final Pattern WHITESPACE = Pattern.compile("[\\s_]+");

	public YateaCandidateReader(Logger logger, TokenNormalization tokenNormalization, StringNormalization stringNormalization) {
		super(logger, tokenNormalization, stringNormalization);
	}

	private static SAXParser createSAXParser() throws ParserConfigurationException, SAXException {
	    SAXParserFactory spf = SAXParserFactory.newInstance();
	    spf.setNamespaceAware(true);
		XMLUtils.ignoreDTD(spf);
	    return spf.newSAXParser();
	}

	@Override
	public YateaResult parseFile(File file) throws SAXException, IOException, ParserConfigurationException {
	    SAXParser saxParser = createSAXParser();
		YateaHandler handler = new YateaHandler(XMLUtils.docBuilder);
	    saxParser.parse(file, handler);
	    return handler.result;
	}

	@Override
	public YateaResult parseStream(InputStream is) throws SAXException, ParserConfigurationException, IOException {
	    SAXParser saxParser = createSAXParser();
		YateaHandler handler = new YateaHandler(XMLUtils.docBuilder);
	    saxParser.parse(is, handler);
	    return handler.result;
	}
	
	public static class YateaResult extends ReaderResult {
		private final Map<String,Candidate> candidates = new HashMap<String,Candidate>();
		private final Collection<Candidate> mnps = new ArrayList<Candidate>();
		private final DefaultMap<Candidate,List<String>> conceptIDs = new DefaultArrayListHashMap<Candidate,String>();

		public Collection<Candidate> getCandidates() {
			return new ArrayList<Candidate>(candidates.values());
		}
		
		public Collection<Candidate> getMNPCandidates() {
			return mnps;
		}
		
		public Map<Candidate,List<String>> getConceptIDs() {
			return conceptIDs;
		}
	}

	private class YateaHandler extends AbstractYateaHandler {
		private final YateaResult result = new YateaResult();
		private final Map<String,String> tokenHeads = new HashMap<String,String>();
		private final Map<String,String> heads = new HashMap<String,String>();
		private final Map<String,Collection<String>> modifiers = new HashMap<String,Collection<String>>();

		private YateaHandler(DocumentBuilder docBuilder) {
			super(null, docBuilder);
		}

		@Override
		protected void handleCandidate() throws XPathExpressionException {
			String id = getCurrentCandidateId();
			if (result.candidates.containsKey(id)) {
				throw new RuntimeException("duplicate term id: " + id);
			}

			String candForm = getForm();
			String candLemma = getLemma();
			String candPOS = getPOS();
			
			String[] tokForms = WHITESPACE.split(candForm);
			String[] tokLemmas = WHITESPACE.split(candLemma);
			String[] tokPOSs = WHITESPACE.split(candPOS);
			
			if (tokForms.length != tokLemmas.length) {
				if (logger != null) {
					logger.warning("mismatch # tokens: form [" + candForm + "] and lemma [" + candLemma + "]. I give up...");
				}
				return;
			}
			if (tokForms.length != tokPOSs.length) {
				String msg = "mismatch # tokens: form [" + candForm + "] and pos [" + candPOS + "]";
				if (logger != null) {
					logger.warning(msg);
				}
				return;
//				String lastPOS = tokPOSs[tokPOSs.length - 1];
//				String[] tokPOSs2 = new String[tokForms.length];
//				System.arraycopy(tokPOSs, 0, tokPOSs2, 0, tokPOSs.length);
//				Arrays.fill(tokPOSs2, tokPOSs.length, tokPOSs2.length, lastPOS);
//				tokPOSs = tokPOSs2;
			}
			
			List<Token> candTokens = new ArrayList<Token>(tokForms.length);
			for (int i = 0; i < tokForms.length; ++i) {
				String form = tokForms[i];
				String lemma = tokLemmas[i];
				String pos = tokPOSs[i];
				Token t = getToken(form, lemma, pos);
				candTokens.add(t);
			}
			Candidate candidate = new Candidate(candTokens);
			result.candidates.put(id, candidate);
			
			boolean mnp = isMNP();
			if (mnp) {
				result.mnps.add(candidate);
			}

			String head = getHeadID();
			tokenHeads.put(id, head);
			
			String saHead = getSuperHeadID();
			if (!saHead.isEmpty()) {
				heads.put(id, saHead);
			}
			
			List<Pair<String,ModifierPosition>> modElts = getModifiersID();
			if (!modElts.isEmpty()) {
				Collection<String> mods = new ArrayList<String>(modElts.size());
				for (Pair<String,ModifierPosition> modElt : modElts) {
					mods.add(modElt.first);
				}
				modifiers.put(id, mods);
			}

			List<Element> conceptElts = evaluateElements("CONCEPT_LIST/CONCEPT_ID");
			if (!conceptElts.isEmpty()) {
				List<String> conceptIDs = result.conceptIDs.safeGet(candidate);
				for (Element conceptElt : conceptElts) {
					String conceptID = conceptElt.getTextContent().trim();
					conceptIDs.add(conceptID);
				}
			}
		}
		
		private Candidate getCandidate(String id) {
			if (result.candidates.containsKey(id)) {
				return result.candidates.get(id);
			}
			throw new RuntimeException("unknown term id: " + id);
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
			Iterator<Map.Entry<String,Candidate>> it = result.candidates.entrySet().iterator();
			for (Map.Entry<String,Candidate> e : Iterators.loop(it)) {
				String id = e.getKey();
				Candidate candidate = e.getValue();
				try {
					if (tokenHeads.containsKey(id)) {
						String tokenHeadId = tokenHeads.get(id);
						Candidate tokenHead = getCandidate(tokenHeadId);
						candidate.setHeadToken(tokenHead.getTokens().get(0));
					}
					if (heads.containsKey(id)) {
						String headId = heads.get(id);
						Candidate head = getCandidate(headId);
						candidate.setHead(head);
					}
					if (modifiers.containsKey(id)) {
						for (String modId : modifiers.get(id)) {
							Candidate mod = getCandidate(modId);
							candidate.addModifier(mod);
						}
					}
				}
				catch (RuntimeException err) {
					YateaCandidateReader.this.logger.warning(err.getMessage());
					YateaCandidateReader.this.logger.warning("removing candidate " + candidate);
					it.remove();
				}
			}
		}
	}
}
