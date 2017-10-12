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

package fr.inra.maiage.bibliome.util.notation;

import java.util.List;
import java.util.Map;

public abstract class StandardNotationVisitor<R,P> {
	public abstract R visit(List<Object> list, P param);
	public abstract R visit(String value, P param);
	public abstract R visit(String key, List<Object> value, P param);
	
	@SuppressWarnings("unchecked")
	public R visit(Object value, P param) {
		if (value instanceof List) {
			return visit((List<Object>) value, param);
		}
		if (value instanceof Map.Entry) {
			Map.Entry<String,List<Object>> map = (Map.Entry<String,List<Object>>) value;
			String key = map.getKey();
			List<Object> list = map.getValue();
			return visit(key, list, param);
		}
		if (value instanceof String) {
			return visit((String) value, param);
		}
		throw new RuntimeException();
	}

	protected static String getHead(List<Object> list) {
		if (!list.isEmpty()) {
			Object first = list.get(0);
			if (first instanceof String) {
				return (String) first;
			}
		}
		return null;
	}
}
