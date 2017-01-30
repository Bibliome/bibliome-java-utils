package org.bibliome.util.pubmed;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.bibliome.util.clio.CLIOException;
import org.bibliome.util.clio.CLIOParser;
import org.bibliome.util.clio.CLIOption;
import org.bibliome.util.filters.AcceptAll;
import org.bibliome.util.filters.AndFilter;
import org.bibliome.util.filters.Filter;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class PubMedSelect extends CLIOParser {
	private Filter<Document> filter = new AcceptAll<Document>();
	private File source = null;
	private File xmlFile = null;
	private File pmidFile = null;

	@CLIOption(value="-help", stop=true)
	public void help() { 
		System.out.print(usage());
	}

	private void addFilter(Filter<Document> filter) {
		this.filter = new AndFilter<Document>(this.filter, filter);
	}
	
	@CLIOption("-pmids")
	public void addPMIDFilter(File pmidFile) throws XPathExpressionException, IOException {
		addFilter(new PMIDFilter(pmidFile));
	}
	
	@CLIOption("-meshUIs")
	public void addUIFilter(File uiFile) throws XPathExpressionException, IOException {
		addFilter(new MeshFilter(uiFile));
	}
	
	@CLIOption("-meshRoots")
	public void addRootsFilter(File meshFile, File rootFile) throws XPathExpressionException, IOException {
		addFilter(new MeshFilter(meshFile, rootFile));
	}
	
	@CLIOption("-xmlFile")
	public void setTarget(File target) {
		this.xmlFile = target;
	}
	
	@CLIOption("-pmidFile")
	public void setPMIDFile(File pmidFile) {
		this.pmidFile = pmidFile;
	}

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		source = new File(arg);
		return false;
	}

	@Override
	public String getResourceBundleName() {
		return PubMedSelect.class.getCanonicalName() + "Help";
	}
	
	public static void main(String[] args) throws CLIOException, SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		PubMedSelect inst = new PubMedSelect();
		if (inst.parse(args)) {
			return;
		}
		if (inst.xmlFile == null) {
			System.err.println("missing XML output file");
			System.exit(1);
		}
		if (inst.source == null) {
			System.err.println("PubMed source file or directory");
			System.exit(1);
		}
		PubMedSelectDOMBuilderHandler h = new PubMedSelectDOMBuilderHandler(inst.filter);
		h.select(inst.source);
		System.err.format("Writing selected articles in %s...\n", inst.xmlFile.getAbsolutePath());
		XMLUtils.writeDOMToFile(h.getTargetDocument(), null, inst.xmlFile);
		if (inst.pmidFile != null) {
			try (PrintStream ps = new PrintStream(inst.pmidFile)) {
				for (String pmid : h.getPMIDs()) {
					ps.println(pmid);
				}
			}
		}
	}
}
