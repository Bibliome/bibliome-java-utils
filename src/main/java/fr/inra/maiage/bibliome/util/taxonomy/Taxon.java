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

package fr.inra.maiage.bibliome.util.taxonomy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import fr.inra.maiage.bibliome.util.taxonomy.reject.RejectName;
import fr.inra.maiage.bibliome.util.taxonomy.saturate.Saturate;

/**
 * A taxon.
 * @author rbossy
 *
 */
public class Taxon {
	/**
	 * Default name type for canonical name.
	 */
	public static final String CANONICAL_NAME_TYPE = "scientific name";
	
	private final String taxid;
	private final String rank;
	private final int division;
	private Taxon parent;
	private final Collection<Taxon> children = new HashSet<Taxon>();
	private final Collection<Name> names = new HashSet<Name>();
	private Name canonicalName;
	
	/**
	 * Creates a taxon with the specified identifier, rank and division.
	 * @param taxid
	 * @param rank
	 * @param division
	 */
	public Taxon(String taxid, String rank, int division) {
		super();
		this.taxid = taxid;
		this.rank = rank;
		this.division = division;
	}

	/**
	 * Returns this taxon identifier.
	 */
	public String getTaxid() {
		return taxid;
	}

	/**
	 * Returns this taxon rank.
	 */
	public String getRank() {
		return rank;
	}

	/**
	 * Returns this taxon division.
	 */
	public int getDivision() {
		return division;
	}

	/**
	 * Returns this taxon parent.
	 */
	public Taxon getParent() {
		return parent;
	}

	/**
	 * Returns this taxon immediate sub-taxa.
	 */
	public Collection<Taxon> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

	/**
	 * Sets this taxon immediate super-taxon.
	 * @param parent
	 */
	public void setParent(Taxon parent) {
		if (this.parent != null)
			this.parent.children.remove(this);
		if (!this.equals(parent)) {
			this.parent = parent;
			if (parent != null)
				parent.children.add(this);
		}
	}
	
	private void getDescendents(Collection<Taxon> result, boolean includeSelf) {
		if (includeSelf)
			result.add(this);
		for (Taxon child : children)
			child.getDescendents(result, true);
	}
	
	/**
	 * Return all descendants of this taxon.
	 * @param includeSelf
	 */
	public Collection<Taxon> getDescendents(boolean includeSelf) {
		Collection<Taxon> result = new HashSet<Taxon>();
		getDescendents(result, includeSelf);
		return result;
	}
	
	/**
	 * Returns all ancestor of this taxon.
	 * @param includeSelf
	 * @param reverse
	 */
	public List<Taxon> getPath(boolean includeSelf, boolean reverse) {
		List<Taxon> result = new ArrayList<Taxon>();
		for (Taxon taxon = includeSelf ? this : parent; taxon != null; taxon = taxon.parent)
			result.add(taxon);
		if (reverse)
			Collections.reverse(result);
		return result;
	}
	
	/**
	 * Return all this taxon names.
	 */
	public Collection<Name> getNames() {
		return Collections.unmodifiableCollection(names);
	}
	
	/**
	 * Adds a name to this taxon.
	 * @param name
	 */
	public void addName(Name name) {
		names.add(name);
		if (name.type.equals(CANONICAL_NAME_TYPE))
			setCanonicalName(name);
	}
	
	/**
	 * Adds a name of the specified type to this taxon.
	 * @param name
	 * @param type
	 */
	public void addName(String name, String type) {
		addName(new Name(name, type));
	}
	
	public boolean addName(RejectName reject, Name name) {
		if (CANONICAL_NAME_TYPE.equals(name.type))
			setCanonicalName(name);
		if (reject.reject(taxid, name))
			return false;
		names.add(name);
		return true;
	}
	
	public boolean addName(RejectName reject, String name, String type) {
		return addName(reject, new Name(name, type));
	}
	
	/**
	 * Removes the specified name from this taxon.
	 * @param name
	 */
	public void removeName(Name name) {
		names.remove(name);
	}
	
	/**
	 * Removes the specified name from this taxon.
	 * @param name
	 */
	public void removeName(String name) {
		names.remove(new Name(name, null));
	}
	
	/**
	 * Removes all names from this taxon rejected by the specified name reject.
	 * @param reject
	 */
	public void reject(RejectName reject) {
		Iterator<Name> it = names.iterator();
		while (it.hasNext()) {
			Name name = it.next();
			if (reject.reject(taxid, name)) {
				it.remove();
			}
		}
	}
	
	/**
	 * Adds names to this taxon according to the specified saturator.
	 * @param saturate
	 */
	public void saturate(Saturate saturate) {
		Collection<Name> toAdd = new HashSet<Name>();
		for (Name name : names)
			toAdd.addAll(saturate.saturate(name));
		names.addAll(toAdd);
	}
	
	public void saturate(RejectName reject, Saturate saturate) {
		Collection<Name> toAdd = new HashSet<Name>();
		for (Name name : names)
			if (!reject.reject(taxid, name))
				toAdd.addAll(saturate.saturate(name));
		names.addAll(toAdd);
	}
	
	/**
	 * Returns this taxon canonical name using the specified type as the canonical name type.
	 */
	public String getCanonicalName() {
		if (canonicalName == null)
			return null;
		return canonicalName.name;
	}

	private void setCanonicalName(Name name) {
		if (canonicalName != null)
			throw new RuntimeException("two canonical names for taxid " + taxid);
		canonicalName = name;
	}
	
	public Taxon getAncestorOfRank(String rank, boolean includeSelf) {
		for (Taxon taxon = includeSelf ? this : parent; taxon != null; taxon = taxon.parent) {
			if (rank.equals(taxon.getRank())) {
				return taxon;
			}
		}
		return null;
	}
}
