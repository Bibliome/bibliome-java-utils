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
