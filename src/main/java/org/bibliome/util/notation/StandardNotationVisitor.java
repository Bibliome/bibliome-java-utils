package org.bibliome.util.notation;

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
