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
import java.util.LinkedHashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.util.mappers.Mappers;

public class Group extends AlvisAEAnnotation {
	private final Collection<String> items = new LinkedHashSet<String>();

	public Group(AnnotationSet annotationSet, String id, String type) {
		super(annotationSet, id, type);
		annotationSet.addGroup(this);
	}
	
	Group(AnnotationSet annotationSet, JSONObject jGrp) {
		super(annotationSet, jGrp);
		for (Object o : (JSONArray) jGrp.get("group")) {
			JSONObject ref = (JSONObject) o;
			addItem((String) ref.get("ann_id"));
		}
		annotationSet.addGroup(this);
	}
	
	public Collection<AlvisAEAnnotation> getItems() {
		return Collections.unmodifiableCollection(Mappers.mappedCollection(getAnnotationSet().ANNOTATION_RESOLVER, items));
	}
	
	public Collection<String> getItemIds() {
		return Collections.unmodifiableCollection(items);
	}
	
	public void addItem(String ref) {
		items.add(ref);
	}
	
	public void addItems(Collection<String> refs) {
		items.addAll(refs);
	}
	
	public void removeItem(String ref) {
		items.remove(ref);
	}
	
	public boolean hasItem(String id) {
		return items.contains(id);
	}

	@Override
	public boolean isTextBound() {
		return false;
	}

	@Override
	public TextBound asTextBound() {
		return null;
	}

	@Override
	public boolean isGroup() {
		return true;
	}

	@Override
	public Group asGroup() {
		return this;
	}

	@Override
	public boolean isRelation() {
		return false;
	}

	@Override
	public Relation asRelation() {
		return null;
	}
	
	public int size() {
		return items.size();
	}

	@Override
	public void toString(StringBuilder sb, boolean withId) {
		openToString(sb, withId);
		boolean first = true;
		for (AlvisAEAnnotation item : getItems()) {
			if (first) {
				first = false;
			}
			else {
				sb.append(", ");
			}
			item.toString(sb, false);
		}
		closeToString(sb);
	}
}
