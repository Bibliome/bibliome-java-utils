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

package org.bibliome.util.alvisae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.bibliome.util.mappers.Mappers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SourceAnnotationCollection {
	private final List<SourceAnnotationReference> sourceRefs = new ArrayList<SourceAnnotationReference>();

	void load(JSONArray sources) {
		for (Object objSrc : sources) {
			JSONObject jSrc = (JSONObject) objSrc;
			int annotationSetId = (int) (long) jSrc.get("set_id");
			String annotationId = (String) jSrc.get("ann_id");
			int status = (int) (long) jSrc.get("status");
			SourceAnnotationReference srcRef = new SourceAnnotationReference(annotationSetId, annotationId, status);
			sourceRefs.add(srcRef);
		}
	}
	
	public Collection<SourceAnnotation> getSources(AlvisAEDocument doc) {
		return Mappers.mappedCollection(doc.SOURCE_ANNOTATION_RESOLVER, sourceRefs);
	}
	
	public Collection<SourceAnnotationReference> getSourceReferences() {
		return Collections.unmodifiableCollection(sourceRefs);
	}
	
	public void add(SourceAnnotationReference src) {
		sourceRefs.add(src);
	}

	void update(Collection<AnnotationSet> headAnnotationSets) {
		ListIterator<SourceAnnotationReference> lit = sourceRefs.listIterator();
		while (lit.hasNext()) {
			SourceAnnotationReference ref = lit.next();
			lit.set(ref.update(headAnnotationSets));
		}
	}
}
