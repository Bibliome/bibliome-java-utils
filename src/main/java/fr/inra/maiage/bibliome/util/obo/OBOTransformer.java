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

package fr.inra.maiage.bibliome.util.obo;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.clio.CLIOException;
import fr.inra.maiage.bibliome.util.clio.CLIOParser;
import fr.inra.maiage.bibliome.util.clio.CLIOption;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

public class OBOTransformer extends CLIOParser {
	private String oboPath;
	private String encoding = "UTF-8";
	private String rdfPath;
	private String flatPath;
	private String root;
	
	public OBOTransformer() {
		super();
	}

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		oboPath = arg;
		return true;
	}
	
	@CLIOption("-encoding")
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	} 
	
	@CLIOption("-rdf")
	public void setRDFPath(String rdfPath) {
		this.rdfPath = rdfPath;
	}
	
	@CLIOption("-flat")
	public void setFlatPath(String flatPath) {
		this.flatPath = flatPath;
	}
	
	@CLIOption("-root")
	public void setRoot(String root) {
		this.root = root;
	}
	
	@Override
	public String getResourceBundleName() {
		return OBOTransformer.class.getCanonicalName() + "Help";
	}
	
	private TargetStream getTargetStream(String path) {
		return new FileTargetStream(encoding, path);
	}
	
	public SourceStream getOBOFile() {
		return new FileSourceStream(encoding, oboPath);
	}
	
	public TargetStream getRDFFile() {
		if (rdfPath == null)
			return null;
		return getTargetStream(rdfPath);
	}
	
	public TargetStream getFlatFile() {
		if (flatPath == null)
			return null;
		return getTargetStream(flatPath);
	}

	/**
	 * @param args
	 * @throws CLIOException 
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws CLIOException, UnsupportedEncodingException, IOException {
		OBOTransformer inst = new OBOTransformer();
		if (!inst.parse(args))
			return;
		Logger logger = Logger.getLogger("obo");
		Ontology onto = new Ontology(logger, inst.getOBOFile());
		TargetStream rdfFile = inst.getRDFFile();
		if (rdfFile != null) {
			if (inst.root == null)
				throw new RuntimeException("option -root is mandatory if -rdf is set, absolute roots: " + Strings.joinStrings(onto.getRoots(), ", "));
			Term rootTerm = onto.getTerm(inst.root);
			if (rootTerm == null)
				throw new RuntimeException(inst.root + " is unknown, absolute roots: " + Strings.joinStrings(onto.getRoots(), ", "));
			writeRDFFile(onto, rdfFile, rootTerm);
		}
		TargetStream flatFile = inst.getFlatFile();
		if (flatFile != null)
			writeFlatFile(onto, flatFile);
	}

	private static void writeFlatFile(Ontology onto, TargetStream flatFile) throws IOException {
		PrintStream out = flatFile.getPrintStream();
		for (Term term : onto.getAllTerms()) {
			StringBuilder tail = new StringBuilder();
			tail.append('\t');
			tail.append(term.getId());
			tail.append('\t');
			tail.append(term.getName());
			tail.append('\t');
			tail.append('/');
			int len = tail.length();
			for (List<Term> path : term.getPaths()) {
				tail.setLength(len);
				for (Term anc : path) {
					tail.append(anc.getId());
					tail.append('/');
				}
				out.print(term.getName());
				out.println(tail);
				for (String syn : term.getSynonyms()) {
					out.print(syn);
					out.println(tail);
				}
			}
//			if (map == null)
//				tail = "\t" + term.getName() + '\t' + term.getId();
//			else
//				tail = "\t" + term.getName() + '\t' + term.getId() + "\tnode_" + map.get(term);
		}
		out.close();
	}

	private static Map<Term,Integer> writeRDFFile(Ontology onto, TargetStream rdfFile, Term rootTerm) throws IOException {
		Map<Term,Integer> result = new HashMap<Term,Integer>();
		result.put(rootTerm, 0);
		for (Term term : onto.getAllTerms())
			if (term != rootTerm)
				result.put(term, result.size());
		PrintStream out = rdfFile.getPrintStream();
		out.println("<?xml version=\"1.0\"?>\n<rdf:RDF\n    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n    xmlns:onto=\"http://www-mig.jouy.inra.fr/alvis-onto#\">\n");
		out.println("    <rdf:Description rdf:about=\"http://www.jouy.inra.fr/alvis-onto\">\n        <rdf:type rdf:resource=\"http://www.jouy.inra.fr/alvis-onto#onto\"/>\n        <onto:node rdf:resource=\"#node_0\"/>\n    </rdf:Description>\n");
		Set<Term> done = new HashSet<Term>();
		writeTermRDF(result, done, out, rootTerm);
		out.println("</rdf:RDF>");
		out.close();
		return result;
	}

	private static void writeTermRDF(Map<Term,Integer> map, Set<Term> done, PrintStream out, Term term) {
		if (done.contains(term))
			return;
		out.println("    <rdf:Description rdf:ID=\"node_" + map.get(term) + "\">");
		out.println("        <onto:name>" + term.getName() + "</onto:name>");
		for (Term child : term.getChildren())
			out.println("        <onto:node rdf:resource=\"#node_" + map.get(child) + "\"/>");
		out.println("    </rdf:Description>\n");
		done.add(term);
		for (Term child : term.getChildren())
			writeTermRDF(map, done, out, child);
	}
}
