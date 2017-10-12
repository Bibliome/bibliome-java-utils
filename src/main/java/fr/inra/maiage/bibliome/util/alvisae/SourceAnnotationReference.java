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

package fr.inra.maiage.bibliome.util.alvisae;

import java.util.Collection;


public class SourceAnnotationReference {
	private final int annotationSetId;
	private final String annotationId;
	private final int status;
	
	public SourceAnnotationReference(int annotationSetId, String annotationId, int status) {
		super();
		this.annotationSetId = annotationSetId;
		this.annotationId = annotationId;
		this.status = status;
	}

	public String getAnnotationId() {
		return annotationId;
	}

	public int getAnnotationSetId() {
		return annotationSetId;
	}

	public int getStatus() {
		return status;
	}
	
	SourceAnnotationReference update(Collection<AnnotationSet> headAnnotationSets) {
		for (AnnotationSet head : headAnnotationSets) {
			if (annotationSetId == head.getId()) {
				return this;
			}
		}
		for (AnnotationSet head : headAnnotationSets) {
			if (head.hasAnnotation(annotationId)) {
				return new SourceAnnotationReference(head.getId(), annotationId, status);
			}
		}
		return this;
	}
}
