package org.bibliome.util.alvisae;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

public class Relation extends AlvisAEAnnotation {
	private final Map<String,String> arguments = new LinkedHashMap<String,String>();

	public Relation(AnnotationSet annotationSet, String id, String type) {
		super(annotationSet, id, type);
		annotationSet.addRelation(this);
	}
	
	Relation(AnnotationSet annotationSet, JSONObject jRel) {
		super(annotationSet, jRel);
		JSONObject args = (JSONObject) jRel.get("relation");
		for (Object o : args.entrySet()) {
			@SuppressWarnings("rawtypes")
			Map.Entry e = (Entry) o;
			JSONObject ref = (JSONObject) e.getValue();
			setArgument((String) e.getKey(), (String) ref.get("ann_id"));
		}
		annotationSet.addRelation(this);
	}
	
	public String getArgumentId(String role) {
		return arguments.get(role);
	}

	public AlvisAEAnnotation getArgument(String role) {
		String argId = arguments.get(role);
		AnnotationSet aset = getAnnotationSet();
		if (aset.hasAnnotation(argId)) {
			return aset.resolveAnnotation(argId);
		}
		return null;
	}
	
	public boolean hasArgument(String role) {
		return arguments.containsKey(role);
	}
	
	public void setArgument(String role, String arg) {
		arguments.put(role, arg);
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
		return false;
	}

	@Override
	public Group asGroup() {
		return null;
	}

	@Override
	public boolean isRelation() {
		return true;
	}

	@Override
	public Relation asRelation() {
		return this;
	}
	
	public Collection<String> getRoles() {
		return Collections.unmodifiableCollection(arguments.keySet());
	}

	@Override
	public void toString(StringBuilder sb, boolean withId) {
		openToString(sb, withId);
		boolean first = true;
		for (Map.Entry<String,String> e : arguments.entrySet()) {
			if (first) {
				first = false;
			}
			else {
				sb.append(", ");
			}
			sb.append(e.getKey());
			sb.append(" = ");
			String argId = e.getValue();
			AlvisAEAnnotation arg = getAnnotationSet().resolveAnnotation(argId);
			arg.toString(sb, false);
		}
		closeToString(sb);
	}
	
	
}
