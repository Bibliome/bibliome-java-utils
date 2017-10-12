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

package fr.inra.maiage.bibliome.util.bionlpst.schema;

import java.util.Collection;
import java.util.HashSet;

import fr.inra.maiage.bibliome.util.bionlpst.BioNLPSTAnnotation;
import fr.inra.maiage.bibliome.util.bionlpst.BioNLPSTException;
import fr.inra.maiage.bibliome.util.bionlpst.BioNLPSTRelation;
import fr.inra.maiage.bibliome.util.bionlpst.Event;
import fr.inra.maiage.bibliome.util.bionlpst.Modification;
import fr.inra.maiage.bibliome.util.bionlpst.Normalization;
import fr.inra.maiage.bibliome.util.bionlpst.TextBound;

public class ModificationSchema extends AnnotationSchema {
	private final Collection<String> types = new HashSet<String>();
	
	public ModificationSchema(DocumentSchema documentSchema, String type) throws BioNLPSTException {
		super(documentSchema, type);
	}

	public void addAnnotationType(String type) {
		types.add(type);
	}

	@Override
	public Void visit(TextBound textBound, Collection<String> param) {
		param.add(unexpectedKind(textBound));
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
		BioNLPSTAnnotation annotation = modification.getAnnotation();
		DocumentSchema.checkType(param, modification, "annotation", types, annotation);
		return null;
	}

	@Override
	protected String getExpectedKind() {
		return "modification";
	}

	@Override
	Collection<String> getReferencedTypes() {
		return new HashSet<String>(types);
	}

	@Override
	public <R,P> R accept(AnnotationSchemaVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}
}
