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
