package org.bibliome.util.bionlpst;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AnnotationWithArgs extends BioNLPSTAnnotation {
	private final Map<String,String> argumentIds = new LinkedHashMap<String,String>();
	private final Map<String,BioNLPSTAnnotation> arguments = new LinkedHashMap<String,BioNLPSTAnnotation>();
	
	protected AnnotationWithArgs(String source, int lineno, BioNLPSTDocument document, Visibility visibility, String id, String type) throws BioNLPSTException {
		super(source, lineno, document, visibility, id, type);
	}

	public Map<String,String> getArgumentIds() {
		return Collections.unmodifiableMap(argumentIds);
	}

	public Map<String,BioNLPSTAnnotation> getArguments() {
		return Collections.unmodifiableMap(arguments);
	}
	
	void addArgumentId(String role, String id) throws BioNLPSTException {
		if (argumentIds.containsKey(role))
			error("duplicate argument " + role);
		argumentIds.put(role, id);
	}

	@Override
	public void resolveIds() throws BioNLPSTException {
		arguments.clear();
		for (Map.Entry<String,String> e : argumentIds.entrySet()) {
			arguments.put(e.getKey(), resolveId(e.getValue()));
		}
	}
}
