package org.bibliome.util.biotopes2012;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibliome.util.Pair;
import org.bibliome.util.Strings;
import org.bibliome.util.clio.CLIOException;
import org.bibliome.util.clio.CLIOParser;
import org.bibliome.util.clio.CLIOption;
import org.bibliome.util.fragments.Fragment;

public class Biotopes2012Diff extends CLIOParser {
	private final List<String> documentDescriptions = new ArrayList<String>();
	private String url = "jdbc:postgresql://bddev:5432/annotation";
	private String schema = "aae_biotopes";
	private String user = "annotation_admin";
	private String password = "annotdba84";
	private String campaignName1 = "Annotateur 1";
	private String campaignName2 = "Annotateur 2";
	private boolean diffGroups = false;
	private boolean diffRelations = false;
	private boolean showMatchTextBound = false;
	private String annotator1 = "Annotateur 1";
	private String annotator2 = "Annotateur 2";
	private Integer firstXhead = null;

	private final Comparator<AlvisAEDocument> DOCUMENT_COMPARATOR = new Comparator<AlvisAEDocument>() {
		@Override
		public int compare(AlvisAEDocument a, AlvisAEDocument b) {
			return a.getDescription().compareTo(a.getDescription());
		}
	};

	public void diff(AlvisAECampaign campaign1, AlvisAECampaign campaign2) {
		List<AlvisAEDocument> documents = new ArrayList<AlvisAEDocument>(campaign1.getDocuments());
		Collections.sort(documents, DOCUMENT_COMPARATOR);
		for (AlvisAEDocument document1 : documents)
			diff(document1, campaign2.getDocumentByDescription(document1.getDescription()));
	}

	private void diff(AlvisAEDocument document1, AlvisAEDocument document2) {
		line(0, "<h2>" + document1.getDescription() + "</h2>");
		line(0, "<strong>Annotateur 1: " + annotator1 + "</strong><br>");
		line(0, "<strong>Annotateur 2: " + annotator2 + "</strong><br>");
		AlvisAEAnnotationSet aset1 = document1.getAnnotationSets().iterator().next();
		AlvisAEAnnotationSet aset2 = document2.getAnnotationSets().iterator().next();
		BiMap<TextBound,TextBound> textMap = diff(aset1.getTextBound(), aset2.getTextBound());
		if (diffGroups) {
			line(1, "<h3>Check: groups contains only text-bound items in " + annotator1 + "</h3>");
			checkAllTextBound(aset1);
			line(1, "<h3>Check: groups contains only text-bound items in " + annotator2 + "</h3>");
			checkAllTextBound(aset2);
			Map<TextBound,Group> groupMap = new HashMap<TextBound,Group>();
			line(1, "<h3>Check: groups are exclusive in " + annotator1 + "</h3>");
			mergeGroups(aset1, groupMap);
			line(1, "<h3>Check: groups are exclusive in " + annotator2 + "</h3>");
			mergeGroups(aset2, groupMap);
			diff(textMap, aset1.getGroups(), aset2.getGroups());
			if (diffRelations)
				diff(textMap, groupMap, aset1.getRelations(), aset2.getRelations());
		}
		ln();
	}

	private void diff(BiMap<TextBound,TextBound> textMap, Map<TextBound,Group> groupMap, Collection<Relation> rels1, Collection<Relation> rels2) {
		line(1, "<h3>Relations</h3>");
		line(1, "<table border=\"1\"><tr><th>Type/Role</th><th>" + annotator1 + "</th><th>" + annotator2 + "</th></tr>");
		for (Relation r1 : rels1)
			if (!hasMatch(textMap, false, groupMap, r1, rels2)) {
				line(2, "<tr><th>" + r1.getType() + "</th><th>" + r1.getId() + "</th><th></th></tr>");
				for (String role : r1.getRoles())
					line(2, "<tr><th>" + role + "</th>" + textCell(r1.getArgument(role)) + textCell(textMap.getForth(DownCastAnnotation.toTextBound(r1.getArgument(role)))) + "</tr>");
			}
		for (Relation r2 : rels2)
			if (!hasMatch(textMap, true, groupMap, r2, rels1)) {
				line(2, "<tr><th>" + r2.getType() + "</th><th></th><th>" + r2.getId() + "</th></tr>");
				for (String role : r2.getRoles()) 
					line(2, "<tr><th>" + role + "</th>" + textCell(textMap.getBack(DownCastAnnotation.toTextBound(r2.getArgument(role)))) + textCell(r2.getArgument(role)) + "</tr>");
			}
		line(1, "</table>");
	}

	private boolean hasMatch(BiMap<TextBound,TextBound> textMap, boolean back, Map<TextBound,Group> groupMap, Relation r, Collection<Relation> rels) {
		for (Relation r2 : rels)
			if (match(textMap, back, groupMap, r, r2))
				return true;
		return false;
	}

	private boolean match(BiMap<TextBound,TextBound> textMap, boolean back, Map<TextBound,Group> groupMap, Relation r1, Relation r2) {
		if (!r1.getType().equals(r2.getType())) {
			return false;
		}
		Collection<String> roles1 = r1.getRoles();
		Collection<String> roles2 = r2.getRoles();
		if (!(roles1.containsAll(roles2) && roles2.containsAll(roles1))) {
			return false;
		}
		for (String role : roles1) {
			TextBound t1 = DownCastAnnotation.toTextBound(r1.getArgument(role));
			if (t1 == null) {
				return false;
			}
			TextBound t2 = DownCastAnnotation.toTextBound(r2.getArgument(role));
			if (t2 == null) {
				return false;
			}
			if (!isArgEquivalent(textMap, back, groupMap, t1, t2)) {
				return false;
			}
		}
		return true;
	}

	private boolean isArgEquivalent(BiMap<TextBound,TextBound> textMap, boolean back, Map<TextBound,Group> groupMap, TextBound t1, TextBound t2) {
		Group g1 = groupMap.get(t1);
		Group g2 = groupMap.get(t2);
		if (g1 == null) {
			if (g2 == null) {
				if (back)
					return textMap.hasPair(t2, t1);
				return textMap.hasPair(t1, t2);
			}
			if (back)
				return g2.getItems().contains(textMap.getBack(t1));
			return g2.getItems().contains(textMap.getForth(t1));
		}
		if (g2 == null) {
			if (back)
				return g1.getItems().contains(textMap.getBack(t2));
			return g1.getItems().contains(textMap.getForth(t2));
		}
		if (back)
			return groupDiff(textMap, g2, g1).hasCommon();
		return groupDiff(textMap, g1, g2).hasCommon();
	}

	private void diff(BiMap<TextBound,TextBound> textMap, Collection<Group> groups1, Collection<Group> groups2) {
		line(1, "<h3>Groups</h3>");
		line(1, "<table border=\"1\"><tr><th>" + annotator1 + "</th><th>Status</th><th>" + annotator2 + "</th></tr>");
		Set<Group> hasSomething = new HashSet<Group>();
		for (Group group1 : groups1) {
			for (Group group2 : groups2) {
				GroupDiff diff = groupDiff(textMap, group1, group2);
				if (diff.hasCommon()) {
					hasSomething.add(group1);
					hasSomething.add(group2);
					//if (diff.isMatch())
					//	continue;
					line(2, "<tr><th class=\"" + group1.getType() + "\">" + group1 + "</th><th></th><th class=\"" + group2.getType() + "\">" + group2 + "</th></tr>");
					for (Pair<TextBound,TextBound> p : diff.missing)
						line(2, "<tr>" + textCell(p.first) + "<td>" + annotator1 + " only</td>" + textCell(p.second) + "</tr>");
					for (Pair<TextBound,TextBound> p : diff.common)
						line(2, "<tr>" + textCell(p.first) + "<td>match</td>" + textCell(p.second) + "</tr>");
					for (Pair<TextBound,TextBound> p : diff.extra)
						line(2, "<tr>" + textCell(p.first) + "<td>" + annotator2 + " only</td>" + textCell(p.second) + "</tr>");
				}
			}
		}
		for (Group group : groups1)
			if (!hasSomething.contains(group)) {
				line(2, "<tr><th class=\"" + group.getType() + "\">" + group + "</th><th></th><th></th></tr>");
				for (AlvisAEAnnotation item : group.getItems()) {
					TextBound tb = DownCastAnnotation.toTextBound(item);
					if (tb == null)
						continue;
					line(2, "<tr>" + textCell(tb) + "<td></td><td></td></tr>");
				}
			}
		for (Group group : groups2)
			if (!hasSomething.contains(group)) {
				line(2, "<tr><th></th><th></th><th class=\"" + group.getType() + "\">" + group + "</th></tr>");
				for (AlvisAEAnnotation item : group.getItems()) {
					TextBound tb = DownCastAnnotation.toTextBound(item);
					if (tb == null)
						continue;
					line(2, "<tr><td></td><td></td>" + textCell(tb) + "</tr>");
				}
			}
		line(1, "</table>");
	}

	private class GroupDiff {
		private final Collection<Pair<TextBound,TextBound>> missing = new HashSet<Pair<TextBound,TextBound>>();
		private final Collection<Pair<TextBound,TextBound>> common = new HashSet<Pair<TextBound,TextBound>>();
		private final Collection<Pair<TextBound,TextBound>> extra = new HashSet<Pair<TextBound,TextBound>>();

		private boolean hasCommon() {
			return !common.isEmpty();
		}

		//		private boolean isMatch() {
		//			return hasCommon() && missing.isEmpty() && extra.isEmpty();
		//		}
	}

	private GroupDiff groupDiff(BiMap<TextBound,TextBound> textMap, Group group1, Group group2) {
		GroupDiff result = new GroupDiff();
		Collection<TextBound> items1 = getTextBound(group1);
		Collection<TextBound> items2 = getTextBound(group2);
		for (TextBound t1 : items1) {
			TextBound t2 = textMap.getForth(t1);
			if (t2 == null)
				continue;
			Pair<TextBound,TextBound> p = new Pair<TextBound,TextBound>(t1,t2);
			if (items2.contains(t2))
				result.common.add(p);
			else
				result.missing.add(p);
		}
		for (TextBound t2 : items2) {
			TextBound t1 = textMap.getBack(t2);
			if (t1 == null)
				continue;
			if (!items1.contains(t1))
				result.extra.add(new Pair<TextBound,TextBound>(t1, t2));
		}
		return result;
	}

	private static Collection<TextBound> getTextBound(Group group) {
		Collection<TextBound> result = new HashSet<TextBound>();
		for (AlvisAEAnnotation a : group.getItems()) {
			TextBound t = DownCastAnnotation.toTextBound(a);
			if (t != null)
				result.add(t);
		}
		return result;
	}

	private static void checkAllTextBound(AlvisAEAnnotationSet aset) {
		Collection<Group> toRemove = new HashSet<Group>();
		line(2, "<table>");
		for (Group group : aset.getGroups()) {
			line(3, "<tr><th>" + group + "</th></tr>");
			int n = checkAllTextBound(group);
			if (n == 0) {
				line(3, "<tr><td>EMPTY: removed</td></tr>");
				toRemove.add(group);
			}
		}
		for (Group g : toRemove)
			aset.removeGroup(g);
		line(2, "</table>");
	}

	private static int checkAllTextBound(Group group) {
		int result = 0;
		for (AlvisAEAnnotation a : group.getItems()) {
			if (DownCastAnnotation.toTextBound(a) == null) {
				line(3, "<tr><td>not text-bound: " + a + "</td></tr>");
			}
			else
				result++;
		}
		return result;
	}

	private static void mergeGroups(AlvisAEAnnotationSet aset, Map<TextBound,Group> groupMap) {
		line(2, "<table>");
		line(3, "<tr><th>First merged</th><th>Second merged</th><th>Common item</th><th>Merged type</th><th>Merged ID</th></tr>");
		while (true) {
			GroupMerge pair = needsMerge(aset, groupMap);
			if (pair == null)
				break;
			String mergedType = pair.first.getType();
			if (!mergedType.equals(pair.second.getType()))
				mergedType = mergedType + " / " + pair.second.getType();
			String mergedId = pair.first.getId() + '/' + pair.second.getId();
			line(3, "<tr><td>" + pair.first + "</td><td>" + pair.second + "</td><td>" + pair.common + "</td><td>" + mergedType + "</td><td>" + mergedId + "</td></tr>");
			Group merge = new Group(aset, mergedId, pair.first.getType());
			for (AlvisAEAnnotation a : pair.first.getItems())
				merge.addItem(a);
			for (AlvisAEAnnotation a : pair.second.getItems())
				merge.addItem(a);
			aset.removeGroup(pair.first);
			aset.removeGroup(pair.second);
			for (Map.Entry<TextBound,Group> e : groupMap.entrySet()) {
				if (e.getValue() == pair.first || e.getValue() == pair.second)
					e.setValue(merge);
			}
		}
		line(2, "</table>");
	}

	private static class GroupMerge extends Pair<Group,Group> {
		private final TextBound common;

		private GroupMerge(Group first, Group second, TextBound common) {
			super(first, second);
			this.common = common;
		}
	}

	private static GroupMerge needsMerge(AlvisAEAnnotationSet aset, Map<TextBound,Group> groupMap) {
		for (Group group : aset.getGroups()) {
			for (TextBound t : getTextBound(group)) {
				Group g = groupMap.get(t);
				if (g == null) {
					groupMap.put(t, group);
					continue;
				}
				if (g == group)
					continue;
				return new GroupMerge(group, g, t);
			}
		}
		return null;
	}


	private static final Comparator<TextBound> TEXT_BOUND_COMPARATOR = new Comparator<TextBound>() {
		@Override
		public int compare(TextBound a, TextBound b) {
			return a.getFragments()[0].getStart() - b.getFragments()[0].getStart();
		}
	};

	private static void line(int indent, Object msg) {
		for (int i = 0; i < indent; ++i)
			System.out.print("    ");
		System.out.println(msg);
	}

	private static void ln() {
		System.out.println();
	}

	private static String textCell(AlvisAEAnnotation a) {
		TextBound t = DownCastAnnotation.toTextBound(a);
		String form = t.getForm();
		if (form.length() > 80)
			form = form.substring(0, form.indexOf(' ')) + " BLA BLA BLA " + form.substring(form.lastIndexOf(' '));
		return "<td class=\"" + t.getType() + "\" title=\"" + Strings.joinStrings(t.getFragments(), ' ') + "&nbsp;&nbsp;&nbsp;&nbsp;" + t.getId() + "\">" + form + "</td>";
	}

	private BiMap<TextBound,TextBound> diff(Collection<TextBound> textBound1, Collection<TextBound> textBound2) {
		line(1, "<h3>Text-bound annotations</h3>");
		line(1, "<table border=\"1\"><tr><th>" + annotator1 + "</th><th>" + annotator2 + "</th><th>Status</th></tr>");
		BiMap<TextBound,TextBound> result = new BiMap<TextBound,TextBound>();
		List<TextBound> rest1 = new ArrayList<>(textBound1);
		Collections.sort(rest1, TEXT_BOUND_COMPARATOR);
		Set<TextBound> rest2 = new HashSet<>(textBound2);
		for (TextBound t1 : rest1) {
			TextBound best = null;
			double score = 0;
			for (TextBound t2 : rest2) {
				double d = diff(t1, t2);
				if (d > score) {
					score = d;
					best = t2;
				}
			}
			if (best == null) {
				//				line(2, t1);
				//				line(2, "[MISSING]");
				//				ln();
				line(2, "<tr>" + textCell(t1) + "<td></td><td>MISS</td></tr>");
			}
			else {
				result.add(t1, best);
				rest2.remove(best);
				if (score == 1) {
					if (!t1.getType().equals(best.getType())) {
						//						line(2, t1);
						//						line(2, best);
						//						line(2, "[TYPE: " + t1.getType() + " / " + best.getType() + ']');
						//						ln();
						line(2, "<tr>" + textCell(t1) + textCell(best) + "<td>TYPE</td></tr>");
					}
					else if (showMatchTextBound) {
						line(2, "<tr>" + textCell(t1) + textCell(best) + "<td>PERFECT</td></tr>");
						//						line(2, t1);
						//						line(2, best);
						//						ln();
					}
				}
				else {
					line(2, "<tr>" + textCell(t1) + textCell(best) + "<td>BOUNDARIES " + (t1.getType().equals(best.getType()) ? "" : " &amp; TYPE") + "</td></tr>");
					//					line(2, t1);
					//					line(2, best);
					//					line(2, "[BOUNDARIES: " + score + ']');
					//					if (!t1.getType().equals(best.getType()))
					//						line(2, "[TYPE: " + t1.getType() + " / " + best.getType() + ']');
					//					ln();
				}
			}
		}
		for (TextBound t2 : rest2) {
			line(2, "<tr><td></td>" + textCell(t2) + "<td>MISS</td></tr>");
			//			line(2, "[EXTRA]");
			//			line(2, t2);
			//			ln();
		}
		line(1, "</table>");
		return result;
	}

	private static double diff(TextBound t1, TextBound t2) {
		double l = t1.getTotalLength() + t2.getTotalLength();
		Fragment[] frags1 = t1.getFragments();
		int overlap = 0;
		for (Fragment f2 : t2.getFragments())
			for (Fragment f1 : frags1)
				overlap += overlap(f1, f2);
		return overlap / (l - overlap);
	}

	private static int overlap(Fragment f1, Fragment f2) {
		if (f1.getEnd() < f2.getStart())
			return 0;
		if (f2.getEnd() < f1.getStart())
			return 0;
		return Math.min(f1.getEnd(), f2.getEnd()) - Math.max(f1.getStart(), f2.getStart());
	}

	public static void main(String[] args) throws SQLException, ClassNotFoundException, CLIOException {
		Biotopes2012Diff inst = new Biotopes2012Diff();
		if (inst.parse(args))
			return;
		AlvisAECampaign campaign1 = new AlvisAECampaign(inst.firstXhead == null ? 4 : inst.firstXhead, inst.campaignName1);
		AlvisAECampaign campaign2 = new AlvisAECampaign(inst.firstXhead == null ? 5 : inst.firstXhead, inst.campaignName2);
		Class.forName("org.postgresql.Driver");
		try (Connection connection = DriverManager.getConnection(inst.url, inst.user, inst.password)) {
			campaign1.load(connection, inst.schema, 1, inst.documentDescriptions, inst.firstXhead != null);
			campaign2.load(connection, inst.schema, 1, inst.documentDescriptions, false);
			inst.diff(campaign1, campaign2);
		}
	}

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		documentDescriptions.add(arg);
		return false;
	}

	@Override
	public String getResourceBundleName() {
		return Biotopes2012Diff.class.getCanonicalName() + "Help";
	}

	@CLIOption("-firstXhead")
	public void setFirstXHead(int campaign) {
		this.firstXhead = campaign;
	}

	@CLIOption("-groups")
	public void setDiffGroups() {
		this.diffGroups = true;
	}

	@CLIOption("-relations")
	public void setDiffRelations() {
		this.diffRelations = true;
		this.diffGroups = true;
	}

	@CLIOption("-show-text-matches")
	public void setShowMatchTextBound() {
		this.showMatchTextBound = true;
	}

	@CLIOption("-annotator1")
	public void setAnnotator1(String annotator1) {
		this.annotator1 = annotator1;
	}

	@CLIOption("-annotator2")
	public void setAnnotator2(String annotator2) {
		this.annotator2 = annotator2;
	}
}
