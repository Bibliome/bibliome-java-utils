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

package org.bibliome.util.alvisae;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.EquivalenceHashSets;
import org.bibliome.util.EquivalenceSets;
import org.bibliome.util.Pair;
import org.bibliome.util.Strings;
import org.bibliome.util.fragments.Fragment;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unused")
public class BiotopesDiff2013 {
	private static void resolveEmbeddedGroups(Group group, Collection<Group> toRemove) {
		Collection<AlvisAEAnnotation> items = new ArrayList<AlvisAEAnnotation>(group.getItems());
		for (AlvisAEAnnotation i : items) {
			if (i.isGroup()) {
				Group sub = i.asGroup();
				resolveEmbeddedGroups(sub, toRemove);
				group.removeItem(sub.getId());
				group.addItems(sub.getItemIds());
				notifyEmbeddedGroup(group, sub);
			}
		}
	}

	private static void resolveEmbeddedGroups(AnnotationSet aset) {
		System.out.println("<p><strong>Embedded groups</strong><br><table>");
		Collection<Group> toRemove = new HashSet<Group>();
		for (Group group : aset.getGroups())
			resolveEmbeddedGroups(group, toRemove);
		for (Group group : toRemove)
			aset.removeGroup(group);
		System.out.println("</table></p>");
	}
	
	private static Pair<Group,Group> needsMerge(AnnotationSet aset, Collection<String> inter) {
		for (Group g1 : aset.getGroups())
			for (Group g2 : aset.getGroups()) {
				if (g1 == g2)
					continue;
				inter.clear();
				inter.addAll(g1.getItemIds());
				inter.retainAll(g2.getItemIds());
				if (!inter.isEmpty())
					return new Pair<Group,Group>(g1, g2);
			}
		return null;
	}
	
	private static void resolveOverlappingGroups(AnnotationSet aset) {
		System.out.println("<p><strong>Check overlapping groups</strong><table>");
		Collection<String> inter = new HashSet<String>();
		while (true) {
			Pair<Group,Group> merge = needsMerge(aset, inter);
			if (merge == null)
				break;
			notifyOverlappingGroups(merge.first, merge.second, inter);
			merge.first.addItems(merge.second.getItemIds());
			aset.removeGroup(merge.second);
		}
		System.out.println("</table></p>");
	}

	private static void diffRelations(AnnotationSet aset1, AnnotationSet aset2, EquivalenceSets<AlvisAEAnnotation> equiv) {
		System.out.println("<h2>Diff relations</h2><p><table><tr><th>" + aset1.getUser() + "</th><th>" + aset2.getUser() + "</th></tr>");
		for (Relation rel1 : aset1.getRelations()) {
			if (!searchEquivalentRelation(rel1, aset2, equiv))
				reportMissingRelation(rel1, aset2, true);
		}
		for (Relation rel2 : aset2.getRelations()) {
			if (!searchEquivalentRelation(rel2, aset1, equiv))
				reportMissingRelation(rel2, aset1, false);
		}
		System.out.println("</table></p>");
	}

	private static boolean searchEquivalentRelation(Relation rel1, AnnotationSet aset, EquivalenceSets<AlvisAEAnnotation> equiv) {
		for (Relation rel2 : aset.getRelations())
			if (isEquivalent(rel1, rel2, equiv))
				return true;
		return false;
	}

	private static boolean isEquivalent(Relation rel1, Relation rel2, EquivalenceSets<AlvisAEAnnotation> equiv) {
		if (!rel1.getType().equals(rel2.getType()))
			return false;
		for (String role : rel1.getRoles()) {
			if (!rel2.hasArgument(role))
				return false;
			if (!equiv.isEquivalent(rel1.getArgument(role), rel2.getArgument(role)))
				return false;
		}
		return true;
	}

	private static final void diffGroups(AnnotationSet aset1, AnnotationSet aset2, Map<TextBound,TextBound> textBoundPairing, EquivalenceSets<AlvisAEAnnotation> equiv) {
		System.out.println("<h2>Diff groups</h2><p><table><tr><th>" + aset1.getUser() + "</th><td></td><th>" + aset2.getUser() + "</th></tr>");
		Collection<Group> all = new HashSet<Group>(aset1.getGroups());
		all.addAll(aset2.getGroups());
		while (!all.isEmpty()) {
			Group gA = all.iterator().next();
			all.remove(gA);
			AnnotationSet asetB;
			boolean left = gA.getAnnotationSet() == aset1;
			if (left)
				asetB = aset2;
			else
				asetB = aset1;
			GroupDiff diff = bestMatch(gA, asetB, textBoundPairing);
			if (diff == null) {
				reportMissingGroup(gA, asetB, left);
				continue;
			}
			all.remove(diff.other);
			equiv.setEquivalent(gA, diff.other);
			for (AlvisAEAnnotation i : gA.getItems())
				equiv.setEquivalent(gA, i);
			for (AlvisAEAnnotation i : diff.other.getItems())
				equiv.setEquivalent(diff.other, i);
			reportGroupMismatch(gA, diff, left, textBoundPairing);
		}
		System.out.println("</table></p>");
	}

	private static final class GroupDiff {
		private final Group other;
		private final Collection<AlvisAEAnnotation> common = new HashSet<AlvisAEAnnotation>();
		private final Collection<AlvisAEAnnotation> extra = new HashSet<AlvisAEAnnotation>();
		private final Collection<AlvisAEAnnotation> missing = new HashSet<AlvisAEAnnotation>();
		
		private GroupDiff(Group other) {
			this.other = other;
		}
	}
	
	private static final GroupDiff bestMatch(Group g1, AnnotationSet aset, Map<TextBound,TextBound> textBoundPairing) {
		GroupDiff result = null;
		for (Group g2 : aset.getGroups()) {
			GroupDiff diff = new GroupDiff(g2);
			for (AlvisAEAnnotation i1 : g1.getItems()) {
				if (!textBoundPairing.containsKey(i1))
					continue;
				if (g2.hasItem(textBoundPairing.get(i1).getId()))
					diff.common.add(i1);
				else
					diff.missing.add(i1);
			}
			if (diff.common.size() == 0)
				continue;
			if ((result != null) && (diff.common.size() < result.common.size()))
				continue;
			result = diff;
			for (AlvisAEAnnotation i2 : g2.getItems()) {
				if (!textBoundPairing.containsKey(i2))
					continue;
				if (!g1.hasItem(textBoundPairing.get(i2).getId()))
					diff.extra.add(i2);
			}
		}
		return result;
	}

	private static Map<TextBound,TextBound> diffTextBounds(AnnotationSet aset1, AnnotationSet aset2, boolean diffOnto) throws IOException {
		System.out.println("<h2>Diff entities</h2><p><table><tr><th>" + aset1.getUser() + "</th><th>diff</th><th>" + aset2.getUser() + "</th></tr>");
		Map<TextBound,TextBound> result = new HashMap<TextBound,TextBound>();
		List<TextBound> all0 = new ArrayList<TextBound>();
		all0.addAll(aset1.getTextBounds());
		all0.addAll(aset2.getTextBounds());
		Collections.sort(all0, TextBound.COMPARATOR);
		Collection<TextBound> all = new LinkedHashSet<TextBound>(all0);
		while (!all.isEmpty()) {
			TextBound tA = all.iterator().next();
			all.remove(tA);
			AnnotationSet asetB;
			boolean left = tA.getAnnotationSet() == aset1;
			if (left) {
				asetB = aset2;
			}
			else {
				asetB = aset1;
			}
			TextBound tB = bestMatch(tA, asetB, all);
			if (tB == null) {
				reportMissingTextBound(tA, asetB, left);
				continue;
			}
			all.remove(tB);
			result.put(tA, tB);
			result.put(tB, tA);
			reportTextBoundMismatch(tA, tB, left, diffOnto);
		}
		System.out.println("</table></p>");
		return result;
	}

	private static final TextBound bestMatch(TextBound t1, AnnotationSet aset, Collection<TextBound> rest) {
		float bestScore = 0;
		TextBound result = null;
		for (TextBound t2 : aset.getTextBounds()) {
			if (!rest.contains(t2))
				continue;
			float score = jaccard(t1, t2);
			if (score == 1)
				return t2;
			if (score > bestScore) {
				bestScore = score;
				result = t2;
			}
		}
		return result;
	}

	private static float jaccard(TextBound t1, TextBound t2) {
		float inter = inter(t1, t2);
		float union = (t1.length() + t2.length()) - inter;
		return inter / union;
	}
	
	private static int inter(TextBound t1, TextBound t2) {
		int result = 0;
		Collection<Fragment> c1 = t1.getFragments();
		Collection<Fragment> c2 = t2.getFragments();
		if (c1.isEmpty() || c2.isEmpty()) {
			System.err.println("EMPTY!");
			return 0;
		}
		Iterator<Fragment> i1 = c1.iterator();
		Iterator<Fragment> i2 = c2.iterator();
		Fragment f1 = i1.next();
		Fragment f2 = i2.next();
		while (true) {
			result += inter(f1, f2);
			if (f1.getEnd() >= f2.getEnd()) {
				if (i2.hasNext())
					f2 = i2.next();
				else
					break;
			}
			if (f1.getEnd() <= f2.getEnd()) {
				if (i1.hasNext())
					f1 = i1.next();
				else
					break;
			}
		}
		return result;
	}

	private static int inter(Fragment f1, Fragment f2) {
		if (f1.getStart() >= f2.getEnd())
			return 0;
		if (f2.getStart() >= f1.getEnd())
			return 0;
		return Math.min(f1.getEnd(), f2.getEnd()) - Math.max(f1.getStart(), f2.getStart());
	}
	
	private static final Pattern CLASS_PATTERN = Pattern.compile("^https://bibliome.jouy.inra.fr/tydirws/ontobiotope2013/projects/23260/semClass/(\\d+)");
	
	private static final Collection<String> NO_CLASS_TYPE = new HashSet<String>(Arrays.asList(
			"Irrelevant",
			"Bacteria",
			"Geographical"
			));
	
	private static void resolveClasses(Map<String,String> changes, TextBound t) {
		List<Object> link = null;
		if (t.hasProperty("MBTO-link"))
			link = t.getProperty("MBTO-link");
		else {
			if (t.hasProperty("MBTO-create"))
				link = t.getProperty("MBTO-create");
			else {
				if (!NO_CLASS_TYPE.contains(t.getType())) {
					notifyNoClass(t);
				}
			}
		}
		if (link != null) {
			StringBuilder revise = new StringBuilder();
			for (Object value : link) {
				Matcher m = CLASS_PATTERN.matcher(value.toString());
				if (m.find()) {
					String mbto = m.group(1);
					t.addProperty("MBTO", mbto);
					if (changes.containsKey(mbto))
						revise.append(changes.get(mbto));
				}
			}
			if (revise.length() > 0) {
				notifyRevision(t, revise.toString());
			}
		}
	}

	private static void resolveClasses(Map<String,String> changes, AnnotationSet aset) {
		System.out.println("<p><strong>Ontology attributions</strong><br><table>");
		for (TextBound t : aset.getTextBounds())
			resolveClasses(changes, t);
		System.out.println("</table></p>");
	}
	
	private static final EquivalenceSets<AlvisAEAnnotation> createEquivalenceSets(Map<TextBound,TextBound> textBoundPairing) {
		EquivalenceSets<AlvisAEAnnotation> result = new EquivalenceHashSets<AlvisAEAnnotation>();
		for (Map.Entry<TextBound,TextBound> e : textBoundPairing.entrySet())
			result.setEquivalent(e.getKey(), e.getValue());
		return result;
	}

	private static void diff(AnnotationSet aset1, AnnotationSet aset2, boolean diffOnto) throws IOException {
		Map<TextBound,TextBound> textBoundPairing = diffTextBounds(aset1, aset2, diffOnto);
		EquivalenceSets<AlvisAEAnnotation> equiv = createEquivalenceSets(textBoundPairing);
		diffGroups(aset1, aset2, textBoundPairing, equiv);
		diffRelations(aset1, aset2, equiv);
	}
	
	private static final String URL = "jdbc:postgresql://bdd:5432/annotation";
	private static final String SCHEMA = "aae_ontobiotope2013";
	private static final String USER = "annotation_admin";
	private static final String PASSWORD = "annotroot;84";

	public static void main(String[] args) throws ClassNotFoundException, SQLException, ParseException, IOException {
		switch (args[0]) {
			case "diff":
				doDiff(Integer.parseInt(args[1]), true);
				break;
			case "diffnoonto":
				doDiff(Integer.parseInt(args[1]), false);
				break;
			case "docs":
				queryDocs(Arrays.asList(args).subList(1, args.length));
				break;
			default:
				throw new RuntimeException();
		}
	}
	
	private static final Connection openConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
	
	private static void queryDocs(List<String> users) throws SQLException, ClassNotFoundException, ParseException {
		try (Connection connection = openConnection()) {
			Campaign campaign = new Campaign(false, SCHEMA, 3);
			LoadOptions options = new LoadOptions();
			options.setTaskName("annotation");
			if (!users.isEmpty())
				options.setUserNames(users);
			options.setLoadContents(false);
			options.setLoadGroups(false);
			options.setLoadRelations(false);
			options.setLoadTextBound(false);
			campaign.load(null, connection, options);
			for (AlvisAEDocument doc : campaign.getDocuments()) {
				Collection<AnnotationSet> asets = doc.getAnnotationSets();
				if (asets.size() >= users.size())
					System.out.println(doc.getDescription() + "\t" + doc.getId());
			}
		}
	}
	
	private static void doDiff(int docId, boolean diffOnto) throws SQLException, ParseException, ClassNotFoundException, IOException {
		try (Connection connection = openConnection()) {
			Campaign campaign = new Campaign(false, SCHEMA, 3);
			LoadOptions options = new LoadOptions();
			options.setTaskName("annotation");
			options.addDocId(docId);
			campaign.load(null, connection, options);
			
			Map<String,String> changes = getChanges();
			
			AlvisAEDocument docs = campaign.getDocuments().iterator().next();
			System.out.println("<h1>" + docs.getDescription() + "</h2>");
			Iterator<AnnotationSet> asets = docs.getAnnotationSets().iterator();
			AnnotationSet aset1 = asets.next();
			AnnotationSet aset2 = asets.next();
			System.out.println("<h2>Prepare (" + aset1.getUser() + ")</h2>");
			if (diffOnto)
				resolveClasses(changes, aset1);
			resolveEmbeddedGroups(aset1);
			resolveOverlappingGroups(aset1);
			System.out.println("<h2>Prepare (" + aset2.getUser() + ")</h2>");
			if (diffOnto)
				resolveClasses(changes, aset2);
			resolveEmbeddedGroups(aset2);
			resolveOverlappingGroups(aset2);
			diff(aset1, aset2, diffOnto);
		}
	}
	
	private static final String CHANGES_PATH = "/var/www/cgi-bin/OntoBiotope_diff-39v40.txt";

	private static final Map<String,String> getChanges() throws IOException {
		Map<String,String> result = new HashMap<String,String>();
		StringBuilder value = new StringBuilder();
		BufferedReader r = new BufferedReader(new FileReader(CHANGES_PATH));
		String key = null;
		while (true) {
			String line = r.readLine();
			if (line == null)
				break;
			line = line.trim();
			if (line.isEmpty()) {
				if (key != null) {
					result.put(key, value.toString());
					key = null;
					value.setLength(0);
				}
				continue;
			}
			if (line.charAt(0) == '\t') {
				value.append(line.substring(1));
				value.append("<br>");
				continue;
			}
			List<String> a = Strings.split(line, '\t', -1);
			key = a.get(2);
			value.append(a.get(0));
			value.append("<br>");
		}
		r.close();
		return result;
	}
	
	private static void notifyNoClass(TextBound t) {
		System.out.println("<tr>");
		cell(t);
		System.out.println("<td>NO CLASS!</td></tr>");		
	}
	
	private static void notifyRevision(TextBound t, String revise) {
		System.out.println("<tr>");
		cell(t);
		System.out.println("<td class=\"revise\">" + revise + "</td></tr>");
	}
	
	private static void notifyEmbeddedGroup(Group group, Group sub) {
		System.out.print("<tr class=\"embedded\">");
		cell(group);
		System.out.println("<td>contains</td>");
		cell(group);
		System.out.println("<td>(removed)</td></tr>");
	}
	
	private static void notifyOverlappingGroups(Group first, Group second, Collection<String> inter) {
		System.out.println("<tr class=\"overlapping\">");
		cell(first);
		System.out.println("<td>overlaps with</td>");
		cell(second);
		System.out.println("<td>(removed), common items:</td>");
		AnnotationSet aset = first.getAnnotationSet();
		for (String id : inter) {
			AlvisAEAnnotation a = aset.resolveAnnotation(id);
			TextBound t = a.asTextBound();
			if (t != null) {
				cell(t);
			}
			Group g = a.asGroup();
			if (g != null) {
				cell(g);
			}
			Relation r = a.asRelation();
			if (r != null) {
				cell(r);
			}
		}
		System.out.println("</tr>");
	}

	private static void reportMissingTextBound(TextBound tA, AnnotationSet asetB, boolean left) {
		System.out.println("<tr>");
		if (left)
			cell(tA);
		else
			System.out.println("<td></td>");
		System.out.println("<td>MISS</td>");
		if (!left)
			cell(tA);
		else
			System.out.println("<td></td>");
	}
	
	private static final boolean classEquals(TextBound t1, TextBound t2) {
		Set<Object> c1 = new HashSet<Object>(t1.getProperty("MBTO"));
		Set<Object> c2 = new HashSet<Object>(t2.getProperty("MBTO"));
		if (c1.size() != c2.size()) {
			return false;
		}
		Iterator<Object> it1 = c1.iterator();
		Iterator<Object> it2 = c2.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			if (!it1.next().equals(it2.next()))
				return false;
		}
		return true;
	}
	
	private static void reportTextBoundMismatch(TextBound tA, TextBound tB, boolean left, boolean diffOnto) throws IOException {
		List<String> mismatch = new ArrayList<String>(3);
		if (jaccard(tA, tB) != 1)
			mismatch.add("BOUNDARIES");
		if (!tA.getType().equals(tB.getType()))
			mismatch.add("TYPE");
		if (diffOnto && !classEquals(tA, tB))
			mismatch.add("ONTO");
		if (mismatch.isEmpty())
			return;
		System.out.println("<tr>");
		cell(left ? tA : tB);
		System.out.print("<td>");
		Strings.join(System.out, mismatch, "<br>");
		System.out.println("</td>");
		cell(left ? tB : tA);
		System.out.print("</tr>");
	}

	private static void reportMissingGroup(Group gA, AnnotationSet asetB, boolean left) {
		System.out.println("<tr>");
		if (left)
			cell(gA);
		System.out.println("<td></td><td></td>");
		if (!left)
			cell(gA);
		System.out.println("</tr>");
	}
	
	private static void reportGroupMismatch(Group gA, GroupDiff diff, boolean left, Map<TextBound,TextBound> textBoundPairing) {
		if (diff.extra.isEmpty() && diff.missing.isEmpty())
			return;
		System.out.println("<tr>");
		cell(left ? gA : diff.other);
		System.out.println("<td></td>");
		cell(left ? diff.other : gA);
		System.out.println("</tr>");
		
		TreeSet<TextBound> items = new TreeSet<TextBound>(TextBound.COMPARATOR);
		for (AlvisAEAnnotation a : diff.missing)
			items.add(a.asTextBound());
		for (AlvisAEAnnotation a : diff.extra)
			items.add(a.asTextBound());
		for (TextBound t : items) {
			System.out.println("<tr>");
			boolean missing = diff.missing.contains(t);
			if (left == missing ) {
				cell(t);
				System.out.println("<td>" + (left ? "&lt;" : "&gt;") + "</td>");
				cell(textBoundPairing.get(t));
			}
			else {
				cell(textBoundPairing.get(t));				
				System.out.println("<td>" + (left ? "&gt;" : "&lt;") + "</td>");
				cell(t);
			}
			System.out.println("</tr>");
		}
	}

	private static void reportMissingRelation(Relation rel, AnnotationSet otherAset, boolean left) {
		System.out.println("<tr>");
		if (left)
			cell(rel);
		System.out.println("<td></td>");
		if (!left)
			cell(rel);
		System.out.println("<tr>");
	}
	
	private static void cell(Relation rel) {
		System.out.println("<td>" + rel.getType() + " (");
		printId(rel);
		System.out.println(")<br>");
		for (String role : rel.getRoles()) {
			System.out.println(role + ": ");
			TextBound arg = (TextBound) rel.getArgument(role);
			System.out.print(arg.getForm("|"));
			System.out.print(" (");
			printId(arg);
			System.out.println(")<br>");
		}
		System.out.println("</td>");
	}

	private static void printId(AlvisAEAnnotation ann) {
		String id = ann.getId();
		int len = id.length();
		System.out.println("<span class=\"id\">" + id.substring(0, 5) + "..." + id.substring(len - 5, len) + "</span>");
	}
	
	private static void printClasses(TextBound t) {
		System.out.print("<span class=\"class\">");
		boolean notFirst = false;
		for (Object value : t.getProperty("MBTO")) {
			if (notFirst)
				System.out.print(" * ");
			else
				notFirst = true;
			System.out.print(value);
		}
		System.out.println("</span>");
	}
	
	private static void cell(TextBound t) {
		System.out.println("<td class=\"" + t.getType() + "\">" + t.getForm("|") + "<br>");
		printId(t);
		System.out.println("<br>start: " + t.getFragments().iterator().next().getStart());
		System.out.println("</td>");
	}
	
	private static void cell(Group g) {
		System.out.println("<td class=\"" + g.getType() + "\">");
		printId(g);
		System.out.println("</td>");
	}
}
