package org.bibliome.util.yatea;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.bibliome.util.xml.XMLUtils;
import org.xml.sax.SAXException;


public class Test {
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		File inputFile = new File(args[0]);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();
		Logger logger = Logger.getAnonymousLogger();
		logger.setLevel(Level.FINE);
		YateaHandler handler = new YateaHandler(logger, docBuilder);
	    SAXParserFactory spf = SAXParserFactory.newInstance();
	    spf.setNamespaceAware(true);
		XMLUtils.ignoreDTD(spf);
	    SAXParser saxParser = spf.newSAXParser();
	    saxParser.parse(inputFile, handler);
	    Map<String,TermCandidate> candidates = handler.getTermCandidates();
	    for (TermCandidate cand : candidates.values()) {
	    	System.out.println(cand.toString() + ", HEAD:" + cand.getHead());
	    }
	}
}
