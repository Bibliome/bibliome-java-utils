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

package fr.inra.maiage.bibliome.util.bionlpst;

public class Normalization extends BioNLPSTAnnotation {
	private final String annotationId;
	private final String referent;
	private BioNLPSTAnnotation annotation;
	
	public Normalization(String source, int lineno, BioNLPSTDocument document, Visibility visibility, String id, String type, String annotationId, String referent) throws BioNLPSTException {
		super(source, lineno, document, visibility, id, type);
		this.annotationId = annotationId;
		this.referent = referent;
	}

	public String getAnnotationId() {
		return annotationId;
	}

	public String getReferent() {
		return referent;
	}

	public BioNLPSTAnnotation getAnnotation() {
		return annotation;
	}

	@Override
	public void resolveIds() throws BioNLPSTException {
		annotation = resolveId(annotationId);
		annotation.addNormalization(this);
	}

	@Override
	public <R,P> R accept(BioNLPSTAnnotationVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	@Override
	public AnnotationKind getKind() {
		return AnnotationKind.NORMALIZATION;
	}
}
