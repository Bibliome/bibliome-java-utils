package org.bibliome.util.obo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.obo.dataadapter.DefaultOBOParser;
import org.obo.dataadapter.OBOParseEngine;
import org.obo.dataadapter.OBOParseException;
import org.obo.datamodel.IdentifiedObject;
import org.obo.datamodel.Link;
import org.obo.datamodel.LinkDatabase;
import org.obo.datamodel.LinkedObject;
import org.obo.datamodel.OBOClass;
import org.obo.datamodel.OBOProperty;
import org.obo.datamodel.OBOSession;
import org.obo.datamodel.Synonym;
import org.obo.util.TermUtil;

public class OBOUtils {
	public static Collection<StringBuilder> getPaths(LinkedObject term) {
		return getPaths(null, term);
	}
	
	public static Collection<LinkedObject> getAncestors(LinkedObject term, boolean includeSelf) {
		return collectAncestors(new LinkedHashSet<LinkedObject>(), term, includeSelf);
	}
	
	private static Collection<LinkedObject> collectAncestors(Collection<LinkedObject> ancestors, LinkedObject term, boolean includeSelf) {
		if (includeSelf) {
			ancestors.add(term);
		}
		for (Link link : term.getParents()) {
			if (link.getType().equals(OBOProperty.IS_A)) {
				LinkedObject parent = link.getParent();
				collectAncestors(ancestors, parent, true);
			}
		}
		return ancestors;
	}

	public static Collection<StringBuilder> getPaths(CharSequence prefix, LinkedObject term) {
		Collection<StringBuilder> result = new ArrayList<StringBuilder>(1);
		for (Link link : term.getParents()) {
			if (link.getType().equals(OBOProperty.IS_A)) {
				for (StringBuilder path : getPaths(prefix, link.getParent())) {
					path.append('/');
					path.append(term.getID());
					result.add(path);
				}
			}
		}
		if (result.isEmpty()) {
			StringBuilder path = new StringBuilder();
			if (prefix != null)
				path.append(prefix);
			path.append('/');
			path.append(term.getID());
			result.add(path);
		}
		return result;
	}
	
	public static JSONObject toJSON(OBOSession session, String rootId) {
		LinkDatabase linkDB = session.getLinkDatabase();
		IdentifiedObject idRoot = linkDB.getObject(rootId);
		LinkedObject linkedRoot = (LinkedObject) idRoot;
		OBOClass root = TermUtil.castToClass(linkedRoot);
		return toJSON(root);
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject toJSON(OBOClass term) {
		JSONObject result = new JSONObject();
		String id = term.getID();
		result.put("intid", id);
		result.put("extid", id);
		result.put("name", term.getName());
		JSONArray synonyms = new JSONArray();
		for (Synonym syn : term.getSynonyms()) {
			synonyms.add(syn.getText());
		}
		result.put("syns", synonyms);
		int weight = 0;
		int depth = 0;
		JSONArray children = new JSONArray();
		for (Link link : term.getChildren()) {
			if (link.getType().equals(OBOProperty.IS_A)) {
				OBOClass child = TermUtil.castToClass(link.getChild());
				JSONObject childJSON = toJSON(child);
				children.add(childJSON);
				weight += ((Integer) childJSON.get("descendantnb")) + 1;
				depth = Math.max(((Integer) childJSON.get("sublevelnb")) + 1, depth);
			}
		}
		result.put("children", children);
		result.put("descendantnb", weight);
		result.put("sublevelnb", depth);
		return result;
	}

	public static final OBOSession parseOBO(Collection<String> sources) throws IOException, OBOParseException {
		DefaultOBOParser parser = new DefaultOBOParser();
		OBOParseEngine engine = new OBOParseEngine(parser);
		engine.setPaths(sources);
		engine.parse();
		return parser.getSession();
	}
	
	public static final OBOSession parseOBO(String... sources) throws IOException, OBOParseException {
		return parseOBO(Arrays.asList(sources));
	}
	
	public static void main(String[] args) throws IOException, OBOParseException {
		OBOSession session = parseOBO(args);
		for (OBOClass term : TermUtil.getTerms(session)) {
			for (StringBuilder path : getPaths(term)) {
				System.out.print(term.getID());
				System.out.print('\t');
				System.out.println(path);
			}
		}
	}
}
