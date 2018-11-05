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
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.util.defaultmap.DefaultArrayListHashMap;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultMap;

public abstract class AlvisAEAnnotation extends AnnotationReference {
	private final String type;
	private final DefaultMap<String,List<Object>> properties = new DefaultArrayListHashMap<String,Object>();
	private final SourceAnnotationCollection sources = new SourceAnnotationCollection();
	
	public AlvisAEAnnotation(AnnotationSet annotationSet, String id, String type) {
		super(annotationSet, id);
		this.type = type;
	}
	
	protected AlvisAEAnnotation(AnnotationSet annotationSet, JSONObject jAnnot) {
		this(annotationSet, (String) jAnnot.get("id"), (String) jAnnot.get("type"));
		JSONObject jProps = (JSONObject) jAnnot.get("properties");
		for (Object o : jProps.entrySet()) {
			@SuppressWarnings("rawtypes")
			Map.Entry e = (Map.Entry) o;
			String key = (String) e.getKey();
			JSONArray values = (JSONArray) e.getValue();
			for (Object v : values)
				addProperty(key, v);
		}
		if (jAnnot.containsKey("sources")) {
			JSONArray jSources = (JSONArray) jAnnot.get("sources");
//			System.err.println("jSources = " + jSources);
			sources.load(jSources);
		}
	}

	public boolean hasProperty(String key) {
		return properties.containsKey(key);
	}
	
	public Collection<String> getPropertyKeys() {
		return Collections.unmodifiableCollection(properties.keySet());
	}

	public List<Object> getProperty(String key) {
		return properties.safeGet(key, false);
	}
	
	public void addProperty(String key, Object value) {
		properties.safeGet(key).add(value);
	}

	public String getType() {
		return type;
	}
	
	public Collection<SourceAnnotation> getSources() {
		return sources.getSources(getDocument());
	}
	
	public Collection<SourceAnnotationReference> getSourceReferences() {
		return sources.getSourceReferences();
	}
	
	void addSource(SourceAnnotationReference src) {
		sources.add(src);
	}
	
	void addSource(AnnotationReference aRef, int status) {
		addSource(new SourceAnnotationReference(aRef.getAnnotationSet().getId(), aRef.getId(), status));
	}
	
	void updateSources(Collection<AnnotationSet> headAnnotationSets) {
		sources.update(headAnnotationSets);
	}
	
	public abstract boolean isTextBound();
	
	public abstract TextBound asTextBound();
	
	public abstract boolean isGroup();
	
	public abstract Group asGroup();
	
	public abstract boolean isRelation();
	
	public abstract Relation asRelation();
	
	public abstract <R,P> R accept(AnnotationVisitor<R,P> visitor, P param);
	
	public abstract void toString(StringBuilder sb, boolean withId);
	
	protected void openToString(StringBuilder sb, boolean withId) {
		sb.append(getType());
		if (withId) {
			sb.append(':');
			sb.append(getId());
		}
		sb.append(" { ");
	}
	
	protected static void closeToString(StringBuilder sb) {
		sb.append(" }");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb, true);
		return sb.toString();
	}
}
