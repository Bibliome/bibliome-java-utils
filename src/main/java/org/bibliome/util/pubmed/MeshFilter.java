package org.bibliome.util.pubmed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

public class MeshFilter extends SetPropertyFilter {
	public MeshFilter(Set<String> include) throws XPathExpressionException {
		super(XMLUtils.xp.compile("/PubmedArticle/MedlineCitation/MeshHeadingList/MeshHeading/DescriptorName"), include);
	}
	
	public MeshFilter(File uiFile) throws XPathExpressionException, IOException {
		this(loadIDs(uiFile));
	}
	
	public MeshFilter(File meshFile, File rootFile) throws XPathExpressionException, IOException {
		this(loadByRoots(meshFile, loadIDs(rootFile)));
	}

	private static Set<String> loadByRoots(File meshFile, Set<String> roots) throws IOException {
		Set<String> result = new HashSet<String>();
		try (BufferedReader br = openVerbose(meshFile)) {
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				String[] cols = line.split("\t");
				String path = cols[0];
				String ui = cols[1];
				String name = cols[2];
				Iterator<String> rootIt = roots.iterator();
				for (String root : Iterators.loop(rootIt)) {
					if (path.startsWith(root)) {
						if (path.equals(root)) {
							System.err.format("    %s: %s\n", ui, name);
							rootIt.remove();
						}
						result.add(ui);
						break;
					}
				}
			}
			if (!roots.isEmpty()) {
				System.err.println("    MISSING: " + Strings.join(roots, ", "));
			}
		}
		return result;
	}
	
	@Override
	protected String getValue(Element e) {
		String result = e.getAttribute("UI");
		return result;
	}
}
