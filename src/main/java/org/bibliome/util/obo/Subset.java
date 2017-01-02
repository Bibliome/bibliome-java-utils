package org.bibliome.util.obo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class Subset {
	private final String id;
	private final String name;
	private final Collection<Term> terms = new HashSet<Term>();
	
	Subset(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Collection<Term> getTerms() {
		return Collections.unmodifiableCollection(terms);
	}
	
	public boolean contains(Term term) {
		return terms.contains(term);
	}
	
	public void add(Term term) {
		terms.add(term);
	}
}
