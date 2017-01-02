package org.bibliome.util.yatea;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathExpressionException;

import org.bibliome.util.Pair;
import org.xml.sax.SAXException;

public class YateaHandler extends AbstractYateaHandler {
	private final Map<String,TermCandidate> termCandidates = new HashMap<String,TermCandidate>();
	
	public YateaHandler(Logger logger, DocumentBuilder docBuilder) {
		super(logger, docBuilder);
	}
	
	@Override
	protected void handleCandidate() throws XPathExpressionException {
		String id = getCurrentCandidateId();
		if (termCandidates.containsKey(id)) {
			throw new RuntimeException("duplicate term id: " + id);
		}
		TermCandidate cand = new TermCandidate();
		termCandidates.put(id, cand);
		cand.setId(id);
		cand.setMnp(isMNP());
		cand.setForm(getForm());
		cand.setLemma(getLemma());
		cand.setSyntacticCategory(getPOS());
		cand.setHead(getHeadID());
		cand.setnOccurrences(getNumberOfOccurrences());
		cand.setConfidence(getConfidence());
		String saHeadId = getSuperHeadID();
		if (!saHeadId.isEmpty()) {
			cand.setSaHead(saHeadId);
		}
		List<Pair<String,ModifierPosition>> modifiers = getModifiersID();
		if (!modifiers.isEmpty()) {
			Pair<String,ModifierPosition> p = modifiers.get(0);
			cand.setSaModifier(p.first);
			cand.setSaPosition(p.second);
		}
		String prep = getPreposition();
		if (!prep.isEmpty()) {
			cand.setSaPrep(prep);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		logger.info("resolving term references (head and modifiers)");
		for (TermCandidate cand : termCandidates.values()) {
			cand.resolveTermIds(termCandidates);
		}
	}

	public Map<String, TermCandidate> getTermCandidates() {
		return Collections.unmodifiableMap(termCandidates);
	}
}
