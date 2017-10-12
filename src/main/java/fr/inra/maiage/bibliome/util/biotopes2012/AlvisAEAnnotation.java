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
