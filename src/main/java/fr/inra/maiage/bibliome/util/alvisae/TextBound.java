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

package fr.inra.maiage.bibliome.util.alvisae;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.util.StringCat;
import fr.inra.maiage.bibliome.util.fragments.Fragment;
import fr.inra.maiage.bibliome.util.fragments.FragmentComparator;
import fr.inra.maiage.bibliome.util.fragments.SimpleFragment;

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
