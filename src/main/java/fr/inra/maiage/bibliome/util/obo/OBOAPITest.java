package fr.inra.maiage.bibliome.util.obo;

import java.io.IOException;

import org.obo.dataadapter.OBOParseException;
import org.obo.datamodel.Link;
import org.obo.datamodel.OBOClass;
import org.obo.datamodel.OBOSession;
import org.obo.util.TermUtil;

public class OBOAPITest {
	public static void main(String[] args) throws IOException, OBOParseException {
		OBOSession session = OBOUtils.parseOBO(args);
		String ontologyVersion = session.getCurrentHistory().getVersion();
		System.out.println("version = " + ontologyVersion);
		for (OBOClass term : TermUtil.getTerms(session)) {
			System.out.println("Term: " + term.getID());
			System.out.println("  Name: " + term.getName());
			System.out.println("  Old paths: " + OBOUtils.getPaths(term));
			System.out.println("  New paths: " + OBOUtils.getPaths(null, term, new String[] { "is_a" }));
			System.out.println("  Ext paths: " + OBOUtils.getPaths(null, term, new String[] { "is_a", "~trait_has_value" }));
			System.out.println("  Parents:");
			for (Link link : term.getParents()) {
				System.out.println("    Link: " + link);
			}
			System.out.println("  Children:");
			for (Link link : term.getChildren()) {
				System.out.println("    Link: " + link);
			}
		}
	}
}
