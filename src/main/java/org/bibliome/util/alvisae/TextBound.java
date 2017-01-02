package org.bibliome.util.alvisae;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibliome.util.StringCat;
import org.bibliome.util.fragments.Fragment;
import org.bibliome.util.fragments.FragmentComparator;
import org.bibliome.util.fragments.SimpleFragment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TextBound extends AlvisAEAnnotation {
	private final SortedSet<Fragment> fragments = new TreeSet<Fragment>(new FragmentComparator<Fragment>());

	public TextBound(AnnotationSet annotationSet, String id, String type, Collection<Fragment> fragments) {
		super(annotationSet, id, type);
		this.fragments.addAll(fragments);
		annotationSet.addTextBound(this);
	}
	
	TextBound(AnnotationSet annotationSet, JSONObject jTxt) {
		super(annotationSet, jTxt);
		for (Object o : (JSONArray) jTxt.get("text")) {
			JSONArray jFrag = (JSONArray) o;
			fragments.add(new SimpleFragment((int) (long) jFrag.get(0), (int) (long) jFrag.get(1)));
		}
		annotationSet.addTextBound(this);
	}

	public Collection<Fragment> getFragments() {
		return Collections.unmodifiableCollection(fragments);
	}

	@Override
	public boolean isTextBound() {
		return true;
	}

	@Override
	public TextBound asTextBound() {
		return this;
	}

	@Override
	public boolean isGroup() {
		return false;
	}

	@Override
	public Group asGroup() {
		return null;
	}

	@Override
	public boolean isRelation() {
		return false;
	}

	@Override
	public Relation asRelation() {
		return null;
	}
	
	public int length() {
		int result = 0;
		for (Fragment f : fragments)
			result += f.getEnd() - f.getStart();
		return result;
	}
	
	public String getForm(String separator) {
		String contents = getDocument().getContents();
		if (contents == null)
			return null;
		StringCat strcat = new StringCat();
		boolean notFirst = false;
		for (Fragment f : fragments) {
			if (notFirst)
				strcat.append(separator);
			else
				notFirst = true;
			strcat.append(contents.substring(f.getStart(), f.getEnd()));
		}
		return strcat.toString();
	}
	
	public static final Comparator<TextBound> COMPARATOR = new Comparator<TextBound>() {
		@Override
		public int compare(TextBound t1, TextBound t2) {
			int s1 = t1.fragments.first().getStart();
			int s2 = t2.fragments.first().getStart();
			if (s1 == s2)
				return t2.fragments.last().getEnd() - t1.fragments.last().getEnd();
			return s1 - s2;
		}
	};
	
	public int fragmentCount() {
		return fragments.size();
	}

	@Override
	public void toString(StringBuilder sb, boolean withId) {
		openToString(sb, withId);
		for (Fragment frag : fragments) {
			sb.append(frag.getStart());
			sb.append('-');
			sb.append(frag.getEnd());
			sb.append(", ");
		}
		sb.append(getForm("|"));
		closeToString(sb);
	}
}
