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

package org.bibliome.util.bionlpst.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bibliome.util.Strings;
import org.bibliome.util.bionlpst.AnnotationWithArgs;
import org.bibliome.util.bionlpst.BioNLPSTAnnotation;
import org.bibliome.util.bionlpst.BioNLPSTException;
import org.bibliome.util.bionlpst.Modification;
import org.bibliome.util.bionlpst.Normalization;
import org.bibliome.util.bionlpst.TextBound;

public abstract class WithArgsSchema extends AnnotationSchema {
	private final Map<String,Collection<String>> argTypes = new HashMap<String,Collection<String>>();
	private final Collection<String> mandatoryArgs = new HashSet<String>();
	
	protected WithArgsSchema(DocumentSchema documentSchema, String type) throws BioNLPSTException {
		super(documentSchema, type);
	}

	public Collection<String> getArgs() {
		return Collections.unmodifiableCollection(argTypes.keySet());
	}
	
	public void addArgumentSchema(String role, Collection<String> types, boolean mandatory) throws BioNLPSTException {
		if (argTypes.containsKey(role))
			throw new BioNLPSTException("duplicate schema for role " + role + " in " + type);
		types = new HashSet<String>(types);
		argTypes.put(role, types);
		if (mandatory)
			mandatoryArgs.add(role);
	}
	
	public void addArgumentSchema(String role, Collection<String> types) throws BioNLPSTException {
		addArgumentSchema(role, types, true);
	}
	
	protected void checkArgs(Collection<String> messages, AnnotationWithArgs annotation) {
		Map<String,BioNLPSTAnnotation> args = annotation.getArguments();
		for (Map.Entry<String,BioNLPSTAnnotation> e : args.entrySet()) {
			String role = e.getKey();
			if (!argTypes.containsKey(role)) {
				String msg = annotation.message("unexpected argument " + role);
				messages.add(msg);
				continue;
			}
			Collection<String> types = argTypes.get(role);
			BioNLPSTAnnotation arg = e.getValue();
			DocumentSchema.checkType(messages, annotation, "argument " + role, types, arg);
		}
		Collection<String> missingArgs = new HashSet<String>();
		for (String role : mandatoryArgs) {
			if (!args.containsKey(role)) {
				missingArgs.add(role);
			}
		}
		if (!missingArgs.isEmpty()) {
			String msg = annotation.message("missing arguments: " + Strings.join(missingArgs, ", "));
			messages.add(msg);
		}
	}

	@Override
	public Void visit(TextBound textBound, Collection<String> param) {
		param.add(unexpectedKind(textBound));
		return null;
	}

	@Override
	public Void visit(Normalization normalization, Collection<String> param) {
		param.add(unexpectedKind(normalization));
		return null;
	}

	@Override
	public Void visit(Modification modification, Collection<String> param) {
		param.add(unexpectedKind(modification));
		return null;
	}

	@Override
	Collection<String> getReferencedTypes() {
		Collection<String> result = new HashSet<String>();
		for (Collection<String> types : argTypes.values()) {
			result.addAll(types);
		}
		return result;
	}
	
	
}
