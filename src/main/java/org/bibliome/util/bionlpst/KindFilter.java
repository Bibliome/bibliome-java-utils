package org.bibliome.util.bionlpst;

import java.util.Collection;
import java.util.EnumSet;

import org.bibliome.util.filters.Filter;

public class KindFilter implements Filter<BioNLPSTAnnotation> {
	private final Collection<AnnotationKind> kinds;

	private KindFilter(AnnotationKind first, AnnotationKind... rest) {
		super();
		this.kinds = EnumSet.of(first, rest);
	}
	
	public void addKind(AnnotationKind kind) {
		kinds.add(kind);
	}

	@Override
	public boolean accept(BioNLPSTAnnotation x) {
		return kinds.contains(x.getKind());
	}
}
