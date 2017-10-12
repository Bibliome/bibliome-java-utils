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

package fr.inra.maiage.bibliome.util.obo;

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
