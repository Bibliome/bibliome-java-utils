package org.bibliome.util.biotopes2012;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

public class Relation extends AlvisAEAnnotation {
	private final Map<String,AlvisAEAnnotation> arguments = new HashMap<String,AlvisAEAnnotation>();

	public Relation(AlvisAEAnnotationSet annotationSet, String id, String type) {
		super(annotationSet, id, type);
		annotationSet.addRelation(this);
	}
	
	public Relation(AlvisAEAnnotationSet annotationSet, JSONObject json) {
		super(annotationSet, json);
		JSONObject r = (JSONObject) json.get("relation");
		for (Object o : r.entrySet()) {
			@SuppressWarnings("rawtypes")
			Map.Entry e = (Entry) o;
			String role = (String) e.getKey();
			JSONObject arg = (JSONObject) e.getValue();
			AlvisAEAnnotation a = annotationSet.getAnnotationByID(arg);
			if (a != null)
				setArgument(role, a);
		}
		annotationSet.addRelation(this);
	}
	
	public Collection<String> getRoles() {
		return Collections.unmodifiableCollection(arguments.keySet());
	}
	
	public AlvisAEAnnotation getArgument(String role) {
		return arguments.get(role);
	}
	
	public void setArgument(String role, AlvisAEAnnotation arg) {
		arguments.put(role, arg);
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
		sb.append(" { ");
		boolean notFirst = false;
		for (Map.Entry<String,AlvisAEAnnotation> e : arguments.entrySet()) {
			if (notFirst)
				sb.append("; ");
			else
				notFirst = true;
			sb.append(e.getKey());
			sb.append(": ");
			sb.append(e.getValue());
		}
		sb.append(" }");
		return sb.toString();
	}
}
