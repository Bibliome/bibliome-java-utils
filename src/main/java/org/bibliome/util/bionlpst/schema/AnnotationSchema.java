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

import org.bibliome.util.bionlpst.BioNLPSTAnnotation;
import org.bibliome.util.bionlpst.BioNLPSTAnnotationVisitor;
import org.bibliome.util.bionlpst.BioNLPSTException;

public abstract class AnnotationSchema implements BioNLPSTAnnotationVisitor<Void,Collection<String>> {
	protected final DocumentSchema documentSchema;
	protected final String type;
	
	protected AnnotationSchema(DocumentSchema documentSchema, String type) throws BioNLPSTException {
		super();
		this.documentSchema = documentSchema;
		this.type = type;
		documentSchema.addAnnotationSchema(this);
	}
	
	public void check(Collection<String> messages, BioNLPSTAnnotation annotation) {
		annotation.accept(this, messages);
	}

	public DocumentSchema getDocumentSchema() {
		return documentSchema;
	}

	public String getType() {
		return type;
	}
	
	protected String unexpectedKind(BioNLPSTAnnotation annotation) {
		return annotation.message("annotations of type " + annotation.getType() + " should be " + getExpectedKind());
	}
	
	protected abstract String getExpectedKind();
	
	abstract Collection<String> getReferencedTypes();
	
	public abstract <R,P> R accept(AnnotationSchemaVisitor<R,P> visitor, P param);
}
