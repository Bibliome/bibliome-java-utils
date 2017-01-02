package org.bibliome.util.biotopes2012;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("rawtypes")
public class AlvisAEProperties extends HashMap<String,List> {
	private static final long serialVersionUID = 1L;

	public AlvisAEProperties() {
		super();
	}

	public void loadJSON(JSONObject json) {
		for (Object o : json.entrySet()) {
			Map.Entry e = (Map.Entry) o;
			String key = (String) e.getKey();
			JSONArray values = (JSONArray) e.getValue();
			setProperty(key, values);
		}
	}
	
	public List getProperty(String key) {
		return get(key);
	}
	
	@SuppressWarnings("unchecked")
	public void setProperty(String key, List values) {
		put(key, new ArrayList(values));
	}
	
	public void setProperty(String key, Object value) {
		List values = Collections.singletonList(value);
		setProperty(key, values);
	}
	
	@SuppressWarnings("unchecked")
	public void addProperty(String key, Object value) {
		if (containsKey(key))
			get(key).add(value);
		else
			setProperty(key, value);
	}
}
