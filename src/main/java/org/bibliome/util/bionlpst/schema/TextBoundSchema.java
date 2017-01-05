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
import java.util.HashSet;
import java.util.List;

import org.bibliome.util.bionlpst.BioNLPSTException;
import org.bibliome.util.bionlpst.BioNLPSTRelation;
import org.bibliome.util.bionlpst.Event;
import org.bibliome.util.bionlpst.Modification;
import org.bibliome.util.bionlpst.Normalization;
import org.bibliome.util.bionlpst.TextBound;
import org.bibliome.util.fragments.Fragment;

public class TextBoundSchema extends AnnotationSchema {
	private final int maxFragments;
	
	public TextBoundSchema(DocumentSchema documentSchema, String type, int maxFragments) throws BioNLPSTException {
		super(documentSchema, type);
		this.maxFragments = maxFragments;
	}

	@Override
	public Void visit(TextBound textBound, Collection<String> param) {
		List<Fragment> fragments = textBound.getFragments();
		if (fragments.size() > maxFragments) {
			String msg = textBound.message("too many fragments, maximum allowed: " + maxFragments);
			param.add(msg);
		}
		return null;
	}

	@Override
	public Void visit(BioNLPSTRelation relation, Collection<String> param) {
		param.add(unexpectedKind(relation));
		return null;
	}

	@Override
	public Void visit(Event event, Collection<String> param) {
		param.add(unexpectedKind(event));
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
	protected String getExpectedKind() {
		return "text bound";
	}

	@Override
	Collection<String> getReferencedTypes() {
		return new HashSet<String>();
	}

	@Override
	public <R,P> R accept(AnnotationSchemaVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}
}
