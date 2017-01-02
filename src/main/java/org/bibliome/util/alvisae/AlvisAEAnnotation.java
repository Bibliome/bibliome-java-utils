package org.bibliome.util.alvisae;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bibliome.util.defaultmap.DefaultArrayListHashMap;
import org.bibliome.util.defaultmap.DefaultMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
