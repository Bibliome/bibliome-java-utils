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

package fr.inra.maiage.bibliome.util.biotopes2012;

import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.util.fragments.Fragment;
import fr.inra.maiage.bibliome.util.fragments.SimpleFragment;

public class TextBound extends AlvisAEAnnotation {
	private final Fragment[] fragments;

	public TextBound(AlvisAEAnnotationSet annotationSet, String id, String type, Fragment[] fragments) {
		super(annotationSet, id, type);
		this.fragments = fragments;
		annotationSet.addTextBound(this);
	}
	
	public TextBound(AlvisAEAnnotationSet annotationSet, JSONObject json) {
		super(annotationSet, json);
		JSONArray fragments = (JSONArray) json.get("text");
		this.fragments = new Fragment[fragments.size()];
		for (int i = 0; i < this.fragments.length; ++i) {
			JSONArray f = (JSONArray) fragments.get(i);
			long start = (long) f.get(0);
			long end = (long) f.get(1);
			this.fragments[i] = new SimpleFragment((int) start, (int) end);
		}
		annotationSet.addTextBound(this);
	}

	public Fragment[] getFragments() {
		return Arrays.copyOf(fragments, fragments.length);
	}

	@Override
	public <R,P> R accept(AlvisAEAnnotationVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getId());
		sb.append(":text:");
		sb.append(getType());
		sb.append(" [");
		for (Fragment f : fragments)
			sb.append(f.toString());
		sb.append("] '");
		getForm(sb);
		sb.append('\'');
		return sb.toString();
	}
	
	public void getForm(StringBuilder sb) {
		String contents = getAnnotationSet().getDocument().getContents();
		boolean notFirst = false;
		for (Fragment f : fragments) {
			if (notFirst)
				sb.append('|');
			else
				notFirst = true;
			sb.append(contents.substring(f.getStart(), f.getEnd()));
		}
	}
	
	public String getForm() {
		StringBuilder sb = new StringBuilder();
		getForm(sb);
		return sb.toString();
	}
	
	public int getTotalLength() {
		int result = 0;
		for (Fragment f : fragments) {
			result += f.getEnd();
			result -= f.getStart();
		}
		return result;
	}
}
