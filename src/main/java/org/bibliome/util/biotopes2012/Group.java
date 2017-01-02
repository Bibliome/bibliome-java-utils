package org.bibliome.util.biotopes2012;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Group extends AlvisAEAnnotation {
	private final Collection<AlvisAEAnnotation> items = new HashSet<AlvisAEAnnotation>();

	public Group(AlvisAEAnnotationSet annotationSet, String id, String type) {
		super(annotationSet, id, type);
		annotationSet.addGroup(this);
	}
	
	public Group(AlvisAEAnnotationSet annotationSet, JSONObject json) {
		super(annotationSet, json);
		JSONArray items = (JSONArray) json.get("group");
		for (Object o : items) {
			AlvisAEAnnotation a = annotationSet.getAnnotationByID((JSONObject) o);
			if (a != null)
				addItem(a);
		}
		annotationSet.addGroup(this);
	}

	public Collection<AlvisAEAnnotation> getItems() {
		return Collections.unmodifiableCollection(items);
	}
	
	public void addItem(AlvisAEAnnotation item) {
		items.add(item);
	}
	
	public int size() {
		return items.size();
	}

	@Override
	public <R,P> R accept(AlvisAEAnnotationVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	@Override
	public String toString() {
		return getId() + ":group:" + getType() + " [" + items.size() + ']';
	}
}
