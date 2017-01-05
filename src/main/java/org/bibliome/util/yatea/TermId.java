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

package org.bibliome.util.yatea;

import java.util.Map;

public class TermId implements TermReference {
	private final String id;
	private TermCandidate candidate;

	public TermId(String id) {
		super();
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isMnp() {
		return getCandidate().isMnp();
	}

	@Override
	public String getForm() {
		return getCandidate().getForm();
	}

	@Override
	public String getLemma() {
		return getCandidate().getLemma();
	}

	@Override
	public String getSyntacticCategory() {
		return getCandidate().getSyntacticCategory();
	}

	@Override
	public TermId getHead() {
		return getCandidate().getHead();
	}

	@Override
	public int getnOccurrences() {
		return getCandidate().getnOccurrences();
	}

	@Override
	public float getConfidence() {
		return getCandidate().getConfidence();
	}

	@Override
	public TermId getSaHead() {
		return getCandidate().getSaHead();
	}

	@Override
	public TermId getSaModifier() {
		return getCandidate().getSaModifier();
	}

	@Override
	public ModifierPosition getSaPosition() {
		return getCandidate().getSaPosition();
	}

	@Override
	public String getSaPrep() {
		return getCandidate().getSaPrep();
	}

	@Override
	public int hashCode() {
		return getCandidate().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return getCandidate().equals(obj);
	}

	@Override
	public String toString() {
		if (candidate == null) {
			return "REF:" + id;
		}
		return candidate.toString();
	}

	public TermCandidate getCandidate() {
		if (candidate == null) {
			throw new RuntimeException("unresolved term identifier");
		}
		return candidate;
	}

	@Override
	public void resolveTermIds(Map<String,TermCandidate> candidatesCollection) {
		if (candidate != null) {
			return;
		}
		if (!candidatesCollection.containsKey(id)) {
			throw new RuntimeException("cannot resolve term id: '" + id + "'");
		}
		candidate = candidatesCollection.get(id);
	}
}
