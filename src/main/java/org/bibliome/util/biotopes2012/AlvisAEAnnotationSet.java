package org.bibliome.util.biotopes2012;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class AlvisAEAnnotationSet {
	private final AlvisAEDocument document;
	private final int id;
	private final int user;
	private final int type;
	private final Collection<TextBound> textBound = new ArrayList<TextBound>();
	private final Collection<Group> groups = new ArrayList<Group>();
	private final Collection<Relation> relations = new ArrayList<Relation>();
	private final Map<String,AlvisAEAnnotation> byID = new HashMap<String,AlvisAEAnnotation>();
	
	public AlvisAEAnnotationSet(AlvisAEDocument document, int id, int user, int type) {
		super();
		this.document = document;
		this.id = id;
		this.user = user;
		this.type = type;
		document.addAnnotationSet(this);
	}

	public AlvisAEDocument getDocument() {
		return document;
	}

	public int getUser() {
		return user;
	}

	public int getType() {
		return type;
	}

	public Collection<TextBound> getTextBound() {
		return Collections.unmodifiableCollection(textBound);
	}

	public Collection<Group> getGroups() {
		return Collections.unmodifiableCollection(groups);
	}

	public Collection<Relation> getRelations() {
		return Collections.unmodifiableCollection(relations);
	}
	
	void addTextBound(TextBound textBound) {
		this.textBound.add(textBound);
		byID.put(textBound.getId(), textBound);
	}
	
	void addGroup(Group group) {
		groups.add(group);
		byID.put(group.getId(), group);
	}
	
	void addRelation(Relation relation) {
		relations.add(relation);
		byID.put(relation.getId(), relation);
	}
	
	public AlvisAEAnnotation getAnnotationByID(String id) {
		return byID.get(id);
	}

	public int getId() {
		return id;
	}
	
	public AlvisAEAnnotation getAnnotationByID(JSONObject id) {
		String annotationID = (String) id.get("ann_id");
		if (id.containsKey("set_id")) {
			int setID = (int) id.get("set_id");
			return document.getAnnotationByID(setID, annotationID);
		}
		return getAnnotationByID(annotationID);
	}
	
	void removeGroup(Group group) {
		groups.remove(group);
	}
}
