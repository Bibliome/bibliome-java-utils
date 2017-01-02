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
