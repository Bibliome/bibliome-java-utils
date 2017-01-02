package org.bibliome.util.obo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Term {
	private final int lineno;
	private String id;
	private String name;
	private final Collection<Subset> subsets = new HashSet<Subset>();
	private final Set<String> synonyms = new HashSet<String>();
	private final Collection<Term> parents = new HashSet<Term>();
	private final Collection<Term> children = new HashSet<Term>();

	Term(int lineno) {
		super();
		this.lineno = lineno;
	}

	public int getLineno() {
		return lineno;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Collection<Subset> getSubsets() {
		return Collections.unmodifiableCollection(subsets);
	}

	public Collection<Term> getParents() {
		return Collections.unmodifiableCollection(parents);
	}

	public Collection<Term> getChildren() {
		return Collections.unmodifiableCollection(children);
	}
	
	public Set<String> getSynonyms() {
		return Collections.unmodifiableSet(synonyms);
	}
	
	public boolean isRoot() {
		return parents.isEmpty();
	}
	
	public void add(Subset subset) {
		subsets.add(subset);
	}
	
	public void addParent(Term parent) {
		parents.add(parent);
	}
	
	public void addChild(Term child) {
		children.add(child);
	}

	public boolean hasSynonym(String s) {
		return synonyms.contains(s);
	}
	
	public void addSynonym(String s) {
		synonyms.add(s);
	}
	
	public Collection<List<Term>> getPaths() {
		if (parents.isEmpty())
			return Collections.singleton(Collections.singletonList(this));
		Collection<List<Term>> result = new ArrayList<List<Term>>();
		for (Term parent : parents) {
			for (List<Term> parentPath : parent.getPaths()) {
				List<Term> path = new ArrayList<Term>(parentPath.size() + 1);
				path.addAll(parentPath);
				path.add(this);
				result.add(path);
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		return id + " (" + name + ")";
	}
}
