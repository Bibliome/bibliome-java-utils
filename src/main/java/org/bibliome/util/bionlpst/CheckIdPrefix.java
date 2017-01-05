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

package org.bibliome.util.bionlpst;

import java.util.ArrayList;
import java.util.Collection;

public enum CheckIdPrefix implements BioNLPSTAnnotationVisitor<Void,Collection<String>> {
	INSTANCE;

	private static void checkPrefix(BioNLPSTAnnotation annotation, String expectedPrefix, Collection<String> param) {
		if (expectedPrefix == null)
			return;
		String id = annotation.getId();
		if (!id.startsWith(expectedPrefix)) {
			String msg = annotation.message("id should start with " + expectedPrefix);
			param.add(msg);
		}
	}
	
	@Override
	public Void visit(TextBound textBound, Collection<String> param) {
		checkPrefix(textBound, "T", param);
		return null;
	}

	@Override
	public Void visit(BioNLPSTRelation relation, Collection<String> param) {
		checkPrefix(relation, "R", param);
		return null;
	}

	@Override
	public Void visit(Event event, Collection<String> param) {
		checkPrefix(event, "E", param);
		return null;
	}

	@Override
	public Void visit(Normalization normalization, Collection<String> param) {
		checkPrefix(normalization, "N", param);
		return null;
	}

	@Override
	public Void visit(Modification modification, Collection<String> param) {
		checkPrefix(modification, "M", param);
		return null;
	}
	
	public static final Collection<String> check(BioNLPSTDocument doc) {
		Collection<String> result = new ArrayList<String>();
		for (BioNLPSTAnnotation a : doc.getAnnotations())
			a.accept(INSTANCE, result);
		return result;
	}
}
