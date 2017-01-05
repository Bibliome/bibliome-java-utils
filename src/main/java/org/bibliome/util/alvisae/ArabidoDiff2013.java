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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bibliome.util.EquivalenceHashSets;
import org.bibliome.util.EquivalenceSets;
import org.bibliome.util.Pair;
import org.bibliome.util.Strings;
import org.bibliome.util.defaultmap.DefaultArrayListHashMap;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.fragments.Fragment;
import org.bibliome.util.fragments.FragmentComparator;
import org.json.simple.parser.ParseException;

public class ArabidoDiff2013 {
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
	
	private static abstract class RelationDiff implements Fragment {
		private final Fragment firstFragment;
		protected final boolean left;
		protected final Relation rel1;
		
		protected RelationDiff(Fragment firstFragment, boolean left, Relation rel1) {
			super();
			this.firstFragment = firstFragment;
			this.left = left;
			this.rel1 = rel1;
		}

		protected abstract void report(RelationReport rr, EquivalenceSets<AlvisAEAnnotation> equiv) throws IOException;

		protected static Fragment minStart(Relation... rels) {
			Fragment result = null;
			for (Relation rel : rels) {
				result = minStartSingle(rel, result);
			}
			return result;
		}
		
		private static Fragment minStartSingle(Relation rel, Fragment result) {
			for (String role : rel.getRoles()) {
				AlvisAEAnnotation arg = rel.getArgument(role);
				Relation relArg = arg.asRelation();
				if (relArg != null) {
					result = minStartSingle(relArg, result);
					continue;
				}
				TextBound tbArg = arg.asTextBound();
				if (tbArg != null) {
					Fragment first = tbArg.getFragments().iterator().next();
					if (result == null || first.getStart() < result.getStart()) {
						result = first;
					}
				}
			}
			return result;
		}
		
		@Override
		public int getStart() {
			return firstFragment.getStart();
		}

		@Override
		public int getEnd() {
			return firstFragment.getEnd();
		}
		
		protected static void reportCell(Relation rel, EquivalenceSets<AlvisAEAnnotation> equiv) {
			System.out.format("<td class=\"%s\">%s <span class=\"id\">%s</span>", rel.getType(), rel.getType(), rel.getId());
			for (String role : rel.getRoles()) {
				reportArg(role, rel.getArgument(role));
			}
			for (String key : rel.getPropertyKeys()) {
				System.out.format("<br>%s = %s", key, Strings.joinStrings(rel.getProperty(key), ", "));
			}
			if (equiv != null) {
				System.out.println("<br><em style=\"font-size: 80%\">");
				for (String role : rel.getRoles()) {
					AlvisAEAnnotation arg = rel.getArgument(role);
					reportArg(role, equiv.getOneEquivalent(arg));
				}
				System.out.println("</em>");
			}
		}

		private static void reportArg(String role, AlvisAEAnnotation arg) {
			if (arg == null) {
				System.out.format("<br>%s: ???", role);
				return;
			}
			String face = null;
			Relation rel = arg.asRelation();
			if (rel != null) {
				face = rel.getType();
			}
			TextBound tb = arg.asTextBound();
			if (tb != null) {
				face = tb.getForm("|");
			}
			System.out.format("<br>%s: <span class=\"%s\">%s <span class=\"id\">%s</span></span>", role, arg.getType(), face, arg.getId());
		}
	}
	
	private static class MissingRelation extends RelationDiff {
		private MissingRelation(boolean left, Relation rel) {
			super(minStart(rel), left, rel);
		}

		private boolean isRealMiss(EquivalenceSets<AlvisAEAnnotation> equiv) {
			for (String role : rel1.getRoles()) {
				AlvisAEAnnotation arg = rel1.getArgument(role);
				AlvisAEAnnotation e = equiv.getOneEquivalent(arg);
				if (e == null) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		protected void report(RelationReport rr, EquivalenceSets<AlvisAEAnnotation> equiv) throws IOException {
			System.out.println("<tr>");
			if (left) {
				rr.totalLeft++;
				rr.missingRight++;
				if (isRealMiss(equiv)) {
					rr.realMissRight++;
				}
				reportCell(rel1, equiv);
				System.out.println("<td align=\"center\">MISSING</td><td></td>");
			}
			else {
				rr.totalRight++;
				rr.missingLeft++;
				if (isRealMiss(equiv)) {
					rr.realMissLeft++;
				}
				System.out.println("<td></td><td align=\"center\">MISSING</td>");
				reportCell(rel1, equiv);
			}
			System.out.println("<tr>");
		}
	}
	
	private static class RelationMismatch extends RelationDiff {
		private final Relation rel2;
		
		private RelationMismatch(boolean left, Relation rel1, Relation rel2) {
			super(minStart(rel1, rel2), left, rel1);
			this.rel2 = rel2;
		}
		
		private boolean sameProperties() {
			for (String key : rel1.getPropertyKeys()) {
				if (!rel2.hasProperty(key)) {
					return false;
				}
				Set<Object> values1 = new HashSet<Object>(rel1.getProperty(key));
				Set<Object> values2 = new HashSet<Object>(rel2.getProperty(key));
				if (!values1.equals(values2)) {
					return false;
				}
			}
			return true;
		}
		
		private String getDiffType(RelationReport rr) {
			if (!rel1.getType().equals(rel2.getType())) {
				rr.typeMismatches++;
				return "TYPE";
			}
			if (sameProperties()) {
				rr.perfectMatches++;
				return null;
			}
			rr.propertyMismatches++;
			return "PROPERTIES";
		}

		@Override
		protected void report(RelationReport rr, EquivalenceSets<AlvisAEAnnotation> equiv) throws IOException {
			rr.totalLeft++;
			rr.totalRight++;
			String diff = getDiffType(rr);
			if (diff != null) {
				System.out.println("<tr>");
				reportCell(left ? rel1 : rel2, null);
				System.out.println("<td align=\"center\">"+diff+"</td>");
				reportCell(left ? rel2 : rel1, null);
				System.out.println("</tr>");
			}
		}
	}
	
	private static class RelationReport {
		private int totalLeft;
		private int totalRight;
		private int perfectMatches;
		private int missingLeft;
		private int missingRight;
		private int realMissLeft;
		private int realMissRight;
		private int typeMismatches;
		private int propertyMismatches;
		
		private void report() {
			System.out.format("<tr><td align=\"right\">%d</td><th>Total</th><td>%d</td></tr>", totalLeft, totalRight);
			System.out.format("<tr><td align=\"right\">%d (%.2f)</td><th>Perfect matches</th><td>%d (%.2f)</td></tr>", perfectMatches, ((float) perfectMatches) / totalLeft, perfectMatches, ((float) perfectMatches) / totalRight);
			System.out.format("<tr><td align=\"right\">%d (%.2f)</td><th>Missing</th><td>%d (%.2f)</td></tr>", missingLeft, ((float) missingLeft) / totalRight, missingRight, ((float) missingRight) / totalLeft);
			System.out.format("<tr><td align=\"right\">%d (%.2f)</td><th>Real miss</th><td>%d (%.2f)</td></tr>", realMissLeft, ((float) realMissLeft) / totalRight, realMissRight, ((float) realMissRight) / totalLeft);
			System.out.format("<tr><td align=\"right\">%d (%.2f)</td><th>Type mismatches</th><td>%d (%.2f)</td></tr>", typeMismatches, ((float) typeMismatches) / totalLeft, typeMismatches, ((float) typeMismatches) / totalRight);
			System.out.format("<tr><td align=\"right\">%d (%.2f)</td><th>Property mismatches</th><td>%d (%.2f)</td></tr>", propertyMismatches, ((float) propertyMismatches) / totalLeft, propertyMismatches, ((float) propertyMismatches) / totalRight);
			float errors = missingLeft + missingRight + typeMismatches + propertyMismatches;
			System.out.format("<tr><td align=\"right\">%.2f</td><th>SER</th><td>%.2f</td></tr>", errors / totalRight, errors / totalLeft);
			int union = perfectMatches + missingLeft + missingRight + typeMismatches + propertyMismatches;
			System.out.format("<tr><td align=\"right\">%d</td><th>Union</th><td>%d</td></tr>", union, union);
			System.out.format("<tr><td align=\"right\">%.2f</td><th>Perfect accord</th><td>%.2f</td></tr>", ((float) perfectMatches) / union, ((float) perfectMatches) / union);
			float relax = perfectMatches + typeMismatches + propertyMismatches;
			System.out.format("<tr><td align=\"right\">%.2f</td><th>Relaxed accord</th><td>%.2f</td></tr>", relax / union, relax / union);
			System.out.format("<tr><td align=\"right\">%.2f</td><th>Miss rate</th><td>%.2f</td></tr>", ((float) missingLeft) / union, ((float) missingRight) / union);
			System.out.format("<tr><td align=\"right\">%.2f</td><th>Real miss rate</th><td>%.2f</td></tr>", ((float) realMissLeft) / union, ((float) realMissRight) / union);
		}
	}
	
	private static void diffRelationsPass(AnnotationSet aset1, AnnotationSet aset2, EquivalenceSets<AlvisAEAnnotation> equiv, List<RelationDiff> diffs, boolean firstOrderPass) {
		for (Relation rel1 : aset1.getRelations()) {
			if (firstOrderPass == rel1.getType().equals("Condition")) {
				continue;
			}
			Relation rel2 = searchEquivalentRelation(rel1, aset2, equiv);
			if (rel2 == null) {
				diffs.add(new MissingRelation(true, rel1));
			}
			else {
				diffs.add(new RelationMismatch(true, rel1, rel2));
				equiv.setEquivalent(rel1, rel2);
			}
		}
		for (Relation rel2 : aset2.getRelations()) {
			if (firstOrderPass == rel2.getType().equals("Condition")) {
				continue;
			}
			Relation rel1 = searchEquivalentRelation(rel2, aset1, equiv);
			if (rel1 == null) {
				diffs.add(new MissingRelation(false, rel2));
			}
		}
	}

	private static void diffRelations(AnnotationSet aset1, AnnotationSet aset2, EquivalenceSets<AlvisAEAnnotation> equiv) throws IOException {
		List<RelationDiff> diffs = new ArrayList<RelationDiff>();
		diffRelationsPass(aset1, aset2, equiv, diffs, true);
		diffRelationsPass(aset1, aset2, equiv, diffs, false);
		System.out.format("<h2>Diff relations</h2><p><table><tr><th>%s / %s</th><th>Diff</th><th>%s / %s</th></tr>\n", aset1.getUser(), aset1.getTask(), aset2.getUser(), aset2.getTask());
		Collections.sort(diffs, new FragmentComparator<Fragment>());
		RelationReport rr = new RelationReport();
		for (RelationDiff rd : diffs) {
			rd.report(rr, equiv);
		}
		System.out.format("<tr><th>%s / %s</th><th>Diff</th><th>%s / %s</th></tr>\n", aset1.getUser(), aset1.getTask(), aset2.getUser(), aset2.getTask());
		rr.report();
		System.out.println("</table></p>");
	}
	
	private static Relation searchEquivalentRelation(Relation rel1, AnnotationSet aset, EquivalenceSets<AlvisAEAnnotation> equiv) {
		for (Relation rel2 : aset.getRelations())
			if (isEquivalent(rel1, rel2, equiv))
				return rel2;
		return null;
	}

	private static boolean isEquivalent(Relation rel1, Relation rel2, EquivalenceSets<AlvisAEAnnotation> equiv) {
		List<AlvisAEAnnotation> args1 = new ArrayList<AlvisAEAnnotation>(2);
		List<AlvisAEAnnotation> args2 = new ArrayList<AlvisAEAnnotation>(2);
		for (String role : rel1.getRoles()) {
			args1.add(rel1.getArgument(role));
		}
		for (String role : rel2.getRoles()) {
			args2.add(rel2.getArgument(role));
		}
		return (equiv.isEquivalent(args1.get(0), args2.get(0)) && equiv.isEquivalent(args1.get(1), args2.get(1))) || (equiv.isEquivalent(args1.get(0), args2.get(1)) && equiv.isEquivalent(args1.get(1), args2.get(0)));
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
	
	private static abstract class TextBoundDiff implements Fragment {
		protected final boolean left;

		protected TextBoundDiff(boolean left) {
			super();
			this.left = left;
		}
		
		protected abstract void report(TextBoundReport tbr) throws IOException;
	}
	
	private static class TextBoundReport {
		private int totalLeft;
		private int totalRight;
		private int perfectMatches;
		private float boundaryMismatches;
		private int typeMismatches;
		private int mismatches;
		private int missingLeft;
		private int missingRight;
		
		private void report() {
			System.out.format("<tr><td align=\"right\">%d</td><th>Total</th><td>%d</td></tr>", totalLeft, totalRight);
			System.out.format("<tr><td align=\"right\">%d (%.2f)</td><th>Perfect matches</th><td>%d (%.2f)</td></tr>", perfectMatches, ((float) perfectMatches) / totalLeft, perfectMatches, ((float) perfectMatches) / totalRight);
			System.out.format("<tr><td align=\"right\">%d (%.2f)</td><th>Missing</th><td>%d (%.2f)</td></tr>", missingLeft, ((float) missingLeft) / totalRight, missingRight, ((float) missingRight) / totalLeft);
			System.out.format("<tr><td align=\"right\">%.2f (%.2f)</td><th>Boundary mismatches</th><td>%.2f (%.2f)</td></tr>", boundaryMismatches, (boundaryMismatches / totalLeft), boundaryMismatches, (boundaryMismatches / totalRight));
			System.out.format("<tr><td align=\"right\">%d (%.2f)</td><th>Type mismatches</th><td>%d (%.2f)</td></tr>", typeMismatches, ((float) typeMismatches) / totalLeft, typeMismatches, ((float) typeMismatches) / totalRight);
			System.out.format("<tr><td align=\"right\">%d (%.2f)</td><th>Mismatches</th><td>%d (%.2f)</td></tr>", mismatches, ((float) mismatches) / totalLeft, mismatches, ((float) mismatches) / totalRight);
			float errors = missingLeft + missingRight + mismatches;
			System.out.format("<tr><td align=\"right\">%.2f</td><th>SER</th><td>%.2f</td></tr>", errors / totalRight, errors / totalLeft);
			int union = perfectMatches + mismatches + missingLeft + missingRight;
			System.out.format("<tr><td align=\"right\">%d</td><th>Union</th><td>%d</td></tr>", union, union);
			System.out.format("<tr><td align=\"right\">%.2f</td><th>Perfect accord</th><td>%.2f</td></tr>", ((float) perfectMatches) / union, ((float) perfectMatches) / union);
			float relaxed = perfectMatches + mismatches;
			System.out.format("<tr><td align=\"right\">%.2f</td><th>Relaxed accord</th><td>%.2f</td></tr>", relaxed / union, relaxed / union);
			System.out.format("<tr><td align=\"right\">%.2f</td><th>Miss rate</th><td>%.2f</td></tr>", ((float) missingLeft) / union, ((float) missingRight) / union);
		}
	}
	
	private static class MissingTextBound extends TextBoundDiff {
		private final TextBound textBound;
		
		private MissingTextBound(boolean left, TextBound textBound) {
			super(left);
			this.textBound = textBound;
		}
		
		@Override
		protected void report(TextBoundReport tbr) {
			System.out.println("<tr>");
			if (left) {
				cell(textBound);
				tbr.missingRight++;
				tbr.totalLeft++;
			}
			else {
				System.out.println("<td></td>");
				tbr.missingLeft++;
				tbr.totalRight++;
			}
			System.out.println("<td align=\"center\">MISS</td>");
			if (!left)
				cell(textBound);
			else
				System.out.println("<td></td>");
		}

		@Override
		public int getStart() {
			return textBound.getFragments().iterator().next().getStart();
		}

		@Override
		public int getEnd() {
			return textBound.getFragments().iterator().next().getEnd();
		}
	}
	
	private static class TextBoundMismatch extends TextBoundDiff {
		private final TextBound tA;
		private final TextBound tB;
		private final float boundaries;
		
		private TextBoundMismatch(boolean left, TextBound tA, TextBound tB) {
			super(left);
			this.tA = tA;
			this.tB = tB;
			this.boundaries = jaccard(tA, tB);
		}

		@Override
		protected void report(TextBoundReport tbr) throws IOException {
			tbr.totalLeft++;
			tbr.totalRight++;
			List<String> mismatch = new ArrayList<String>(3);
			if (boundaries != 1) {
				mismatch.add(String.format("BOUNDARIES (%.2f)", boundaries));
				tbr.boundaryMismatches += 1 - boundaries;
			}
			if (!tA.getType().equals(tB.getType())) {
				mismatch.add("TYPE");
				tbr.typeMismatches++;
			}
			if (mismatch.isEmpty()) {
				tbr.perfectMatches++;
				return;
			}
			tbr.mismatches++;
			System.out.println("<tr>");
			cell(left ? tA : tB);
			System.out.print("<td align=\"center\">");
			Strings.join(System.out, mismatch, "<br>");
			System.out.println("</td>");
			cell(left ? tB : tA);
			System.out.print("</tr>");
		}

		@Override
		public int getStart() {
			return tA.getFragments().iterator().next().getStart();
		}

		@Override
		public int getEnd() {
			return tA.getFragments().iterator().next().getEnd();
		}
	}

	private static Map<TextBound,TextBound> diffTextBounds(AnnotationSet aset1, AnnotationSet aset2) throws IOException {
		System.out.format("<h2>Diff entities</h2><p><table><tr><th>%s / %s</th><th>diff</th><th>%s / %s</th></tr>\n", aset1.getUser(), aset1.getTask(), aset2.getUser(), aset2.getTask());
		DefaultMap<TextBound,List<TextBound>> best = new DefaultArrayListHashMap<TextBound,TextBound>();
		for (TextBound tb1 : aset1.getTextBounds()) {
			for (TextBound tb2 : aset2.getTextBounds()) {
				float s = jaccard(tb1, tb2);
				if (s != 0) {
					best.safeGet(tb1).add(tb2);
					best.safeGet(tb2).add(tb1);
				}
			}
		}
		for (Map.Entry<TextBound,List<TextBound>> e : best.entrySet()) {
			final TextBound tb = e.getKey();
			Collections.sort(e.getValue(), new Comparator<TextBound>() {
				@Override
				public int compare(TextBound o1, TextBound o2) {
					return Float.compare(jaccard(o2, tb), jaccard(o1, tb));
				}
			});
		}
		Map<TextBound,TextBound> result = new HashMap<TextBound,TextBound>();
		List<TextBoundDiff> diffs = new ArrayList<TextBoundDiff>();
		boolean desperate = false;
		while (!best.isEmpty()) {
			Collection<TextBound> toRemove = new ArrayList<TextBound>(2);
			for (Map.Entry<TextBound,List<TextBound>> e : best.entrySet()) {
				TextBound tA = e.getKey();
				AnnotationSet aset = tA.getAnnotationSet();
				List<TextBound> matches = e.getValue();
				if (matches.isEmpty()) {
					diffs.add(new MissingTextBound(aset1 == aset, tA));
					toRemove.add(tA);
					break;
				}
				TextBound tB = matches.get(0);
				TextBound back = best.get(tB).get(0);
				if (back == tA || desperate) {
					diffs.add(new TextBoundMismatch(aset1 == aset, tA, tB));
					toRemove.add(tA);
					toRemove.add(tB);
					result.put(tA, tB);
					result.put(tB, tA);
					break;
				}
			}
			desperate = toRemove.isEmpty();
			best.keySet().removeAll(toRemove);
			for (List<TextBound> m : best.values()) {
				m.removeAll(toRemove);
			}
		}
		Collections.sort(diffs, new FragmentComparator<Fragment>());
		TextBoundReport tbr = new TextBoundReport();
		for (TextBoundDiff tbd : diffs) {
			tbd.report(tbr);
		}
		System.out.format("<tr><th>%s / %s</th><th>diff</th><th>%s / %s</th></tr>\n", aset1.getUser(), aset1.getTask(), aset2.getUser(), aset2.getTask());
		tbr.report();
		System.out.println("</table></p>");
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
		
	private static final EquivalenceSets<AlvisAEAnnotation> createEquivalenceSets(Map<TextBound,TextBound> textBoundPairing) {
		EquivalenceSets<AlvisAEAnnotation> result = new EquivalenceHashSets<AlvisAEAnnotation>();
		for (Map.Entry<TextBound,TextBound> e : textBoundPairing.entrySet())
			result.setEquivalent(e.getKey(), e.getValue());
		return result;
	}

	private static void diff(AnnotationSet aset1, AnnotationSet aset2) throws IOException {
		Map<TextBound,TextBound> textBoundPairing = diffTextBounds(aset1, aset2);
		EquivalenceSets<AlvisAEAnnotation> equiv = createEquivalenceSets(textBoundPairing);
		diffGroups(aset1, aset2, textBoundPairing, equiv);
		diffRelations(aset1, aset2, equiv);
	}
	
	private static final String URL = "jdbc:postgresql://bddev:5432/annotation";
	private static final String SCHEMA = "aae_new_arabido";
	private static final String USER = "annotation_admin";
	private static final String PASSWORD = "annotdba84";

	public static void main(String[] args) throws ClassNotFoundException, SQLException, ParseException, IOException {
		switch (args[0]) {
			case "diff":
				doDiff(Integer.parseInt(args[1]), args[2], args[3], args[4], args[5]);
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
	
	private static final Comparator<AlvisAEDocument> DOCUMENT_COMPARATOR = new Comparator<AlvisAEDocument>() {
		@Override
		public int compare(AlvisAEDocument o1, AlvisAEDocument o2) {
			return o1.getDescription().compareTo(o2.getDescription());
		}
	};
	
	private static void queryDocs(List<String> args) throws SQLException, ClassNotFoundException, ParseException {
		try (Connection connection = openConnection()) {
			Campaign campaign = new Campaign(false, SCHEMA, 11);
			LoadOptions options = new LoadOptions();
			options.setLoadContents(false);
			options.setLoadGroups(false);
			options.setLoadRelations(false);
			options.setLoadTextBound(false);
			campaign.loadDocuments(null, connection, options);
			options.setTaskName(args.get(0));
			if (args.size() > 2) {
				options.addUserName(args.get(2));
			}
			campaign.loadAnnotationSets(null, connection, options);
			options.setTaskName(args.get(1));
			options.clearUserNames();
			if (args.size() > 3) {
				options.addUserName(args.get(3));
			}
			campaign.loadAnnotationSets(null, connection, options);
			List<AlvisAEDocument> docs = new ArrayList<AlvisAEDocument>();
			for (AlvisAEDocument doc : campaign.getDocuments()) {
				Collection<AnnotationSet> asets = doc.getAnnotationSets();
				if (asets.size() + 2 >= args.size()) {
					docs.add(doc);
				}
			}
			Collections.sort(docs, DOCUMENT_COMPARATOR);
			for (AlvisAEDocument doc : docs) {
				System.out.println(doc.getDescription() + "\t" + doc.getId());
			}
		}
	}
	
	private static void doDiff(int docId, String annotator1, String annotator2, String task1, String task2) throws SQLException, ParseException, ClassNotFoundException, IOException {
		try (Connection connection = openConnection()) {
			Campaign campaign = new Campaign(false, SCHEMA, 11);
			LoadOptions options = new LoadOptions();
			options.addDocId(docId);
			campaign.loadDocuments(null, connection, options);
			options.addUserName(annotator1);
			options.setTaskName(task1);
			campaign.loadAnnotationSets(null, connection, options);
			options.clearUserNames();
			options.addUserName(annotator2);
			options.setTaskName(task2);
			campaign.loadAnnotationSets(null, connection, options);
			
			AlvisAEDocument docs = campaign.getDocuments().iterator().next();
			System.out.println("<h1>" + docs.getDescription() + "</h2>");
			Iterator<AnnotationSet> asets = docs.getAnnotationSets().iterator();
			AnnotationSet aset1 = asets.next();
			AnnotationSet aset2 = asets.next();
			if (aset1.getUser().equals(annotator2)) {
				AnnotationSet as = aset1;
				aset1 = aset2;
				aset2 = as;
			}
			System.out.println("<h2>Prepare (" + aset1.getUser() + ")</h2>");
			resolveEmbeddedGroups(aset1);
			resolveOverlappingGroups(aset1);
			System.out.println("<h2>Prepare (" + aset2.getUser() + ")</h2>");
			resolveEmbeddedGroups(aset2);
			resolveOverlappingGroups(aset2);
			diff(aset1, aset2);
		}
	}
	
	private static void notifyEmbeddedGroup(Group group, Group sub) {
		System.out.print("<tr class=\"embedded\">");
		cell(group);
		System.out.println("<td>contains</td>");
		cell(sub);
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

	private static void reportMissingGroup(Group gA, @SuppressWarnings("unused") AnnotationSet asetB, boolean left) {
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
	
	private static void cell(Relation rel) {
		System.out.println("<td class=\"" + rel.getType() + "\">" + rel.getType() + " (");
		printId(rel);
		System.out.println(")<br>");
		for (String role : rel.getRoles()) {
			System.out.println(role + ": ");
			AlvisAEAnnotation arg = rel.getArgument(role);
			if (arg instanceof TextBound) {
				System.out.print(((TextBound) arg).getForm("|"));
			}
			else {
				System.out.print(arg.getType());
			}
			System.out.print(" (");
			printId(arg);
			System.out.println(")<br>");
		}
		System.out.println("</td>");
	}

	private static void printId(AlvisAEAnnotation ann) {
		String id = ann.getId();
//		int len = id.length();
		System.out.println("<span class=\"id\">" + id + "</span>");
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
