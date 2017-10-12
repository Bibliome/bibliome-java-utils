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

package fr.inra.maiage.bibliome.util.yatea;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.mappers.Mapper;
import fr.inra.maiage.bibliome.util.mappers.Mappers;
import fr.inra.maiage.bibliome.util.xml.TagElementDOMBuilderHandler;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

public abstract class AbstractYateaHandler extends TagElementDOMBuilderHandler {
	protected final Logger logger;
	private int nTerms = 0;
	private Element currentCandidateElement;
	private String currentCandidateId;
	
	protected AbstractYateaHandler(Logger logger, DocumentBuilder docBuilder) {
		super(docBuilder, "TERM_CANDIDATE");
		this.logger = logger;
	}
	
	protected Element getCurrentCandidateElement() {
		return currentCandidateElement;
	}

	protected String getCurrentCandidateId() {
		return currentCandidateId;
	}
	
	protected String evaluateRawString(String expr) throws XPathExpressionException {
		return XMLUtils.evaluateString(expr, currentCandidateElement);
	}
	
	protected String evaluateString(String expr) throws XPathExpressionException {
		return evaluateRawString(expr).trim();
	}
	
	protected List<Element> evaluateElements(String expr) throws XPathExpressionException {
		return XMLUtils.evaluateElements(expr, currentCandidateElement);
	}
	
	protected boolean isMNP() throws XPathExpressionException {
		return evaluateString("@MNP_STATUS").equals("1");		
	}
	
	protected String getForm() throws XPathExpressionException {
		return evaluateString("FORM");
	}
	
	protected String getLemma() throws XPathExpressionException {
		return evaluateString("LEMMA");
	}
	
	protected String getPOS() throws XPathExpressionException {
		return evaluateString("MORPHOSYNTACTIC_FEATURES/SYNTACTIC_CATEGORY");
	}
	
	protected String getHeadID() throws XPathExpressionException {
		return evaluateString("HEAD");
	}
	
	protected int getNumberOfOccurrences() throws XPathExpressionException {
		return Integer.parseInt(evaluateRawString("NUMBER_OCCURRENCES"));
	}
	
	protected float getConfidence() throws XPathExpressionException {
		return Float.parseFloat(evaluateRawString("TERM_CONFIDENCE"));
	}
	
	private static Mapper<Element,Pair<String,ModifierPosition>> MODIFIER_MAPPER = new Mapper<Element,Pair<String,ModifierPosition>>() {
		@Override
		public Pair<String,ModifierPosition> map(Element x) {
			String modId = x.getTextContent().trim();
			ModifierPosition modPos = null;
			if (x.hasAttribute("@POSITION")) {
				modPos = ModifierPosition.valueOf(x.getAttribute("@POSITION"));
			}
			return new Pair<String,ModifierPosition>(modId, modPos);
		}
	};
	
	protected String getSuperHeadID() throws XPathExpressionException {
		return evaluateString("SYNTACTIC_ANALYSIS/HEAD");
	}
	
	protected List<Pair<String,ModifierPosition>> getModifiersID() throws XPathExpressionException {
		List<Element> modElts = evaluateElements("SYNTACTIC_ANALYSIS/MODIFIER");
		List<Pair<String,ModifierPosition>> result = new ArrayList<Pair<String,ModifierPosition>>(modElts.size());
		return Mappers.apply(MODIFIER_MAPPER, modElts, result);
	}
	
	protected String getPreposition() throws XPathExpressionException {
		return evaluateString("SYNTACTIC_ANALYSIS/PREP");
	}

	@Override
	protected void handleDOMTree(Document doc) throws Exception {
		currentCandidateElement = doc.getDocumentElement();
		currentCandidateId = evaluateString("ID");
		if ((++nTerms) % 1000 == 0) {
			if (logger != null) {
				logger.fine(String.format("read % 8d terms", nTerms));
			}
		}
		handleCandidate();
		currentCandidateElement = null;
		currentCandidateId = null;
	}
	
	protected abstract void handleCandidate() throws XPathExpressionException;
}
