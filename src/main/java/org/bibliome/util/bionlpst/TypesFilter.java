package org.bibliome.util.bionlpst;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.bibliome.util.filters.Filter;

public class TypesFilter implements Filter<BioNLPSTAnnotation> {
	private final Collection<String> types = new HashSet<String>();

	public TypesFilter(String... types) {
		super();
		this.types.addAll(Arrays.asList(types));
	}
	
	public void addType(String type) {
		types.add(type);
	}

	@Override
	public boolean accept(BioNLPSTAnnotation x) {
		return types.contains(x.getType());
	}
}
