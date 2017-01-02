package org.bibliome.util.biotopes2012;

import org.json.simple.JSONObject;

public abstract class AlvisAEAnnotation {
	private final AlvisAEAnnotationSet annotationSet;
	private final String id;
	private final String type;
	private final AlvisAEProperties properties = new AlvisAEProperties();

	protected AlvisAEAnnotation(AlvisAEAnnotationSet annotationSet, String id, String type) {
		super();
		this.annotationSet = annotationSet;
		this.id = id;
		this.type = type;
	}
	
	protected AlvisAEAnnotation(AlvisAEAnnotationSet annotationSet, JSONObject json) {
		super();
		this.annotationSet = annotationSet;
		this.id = (String) json.get("id");
		this.type = (String) json.get("type");
		this.properties.loadJSON((JSONObject) json.get("properties"));
	}

	public String getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}

	public AlvisAEAnnotationSet getAnnotationSet() {
		return annotationSet;
	}

	public abstract <R,P> R accept(AlvisAEAnnotationVisitor<R,P> visitor, P param);

	public AlvisAEProperties getProperties() {
		return properties;
	}
}
