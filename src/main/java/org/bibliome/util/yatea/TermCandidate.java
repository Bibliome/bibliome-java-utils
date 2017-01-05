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

public class TermCandidate implements TermReference {
	private String id;
	private boolean mnp;
	private String form;
	private String lemma;
	private String syntacticCategory;
	private TermId head;
	private int nOccurrences;
	private float confidence;
	private TermId saHead;
	private TermId saModifier;
	private ModifierPosition saPosition;
	private String saPrep;

	public TermCandidate() {
		super();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isMnp() {
		return mnp;
	}

	@Override
	public String getForm() {
		return form;
	}

	@Override
	public String getLemma() {
		return lemma;
	}

	@Override
	public String getSyntacticCategory() {
		return syntacticCategory;
	}

	@Override
	public TermId getHead() {
		return head;
	}

	@Override
	public int getnOccurrences() {
		return nOccurrences;
	}

	@Override
	public float getConfidence() {
		return confidence;
	}

	@Override
	public TermId getSaHead() {
		return saHead;
	}

	@Override
	public TermId getSaModifier() {
		return saModifier;
	}

	@Override
	public ModifierPosition getSaPosition() {
		return saPosition;
	}

	@Override
	public String getSaPrep() {
		return saPrep;
	}

	void setId(String id) {
		this.id = id;
	}

	void setMnp(boolean mnp) {
		this.mnp = mnp;
	}

	void setForm(String form) {
		this.form = form;
	}

	void setLemma(String lemma) {
		this.lemma = lemma;
	}

	void setSyntacticCategory(String syntacticCategory) {
		this.syntacticCategory = syntacticCategory;
	}

	void setHead(TermId head) {
		this.head = head;
	}
	
	void setHead(String id) {
		setHead(new TermId(id));
	}

	void setnOccurrences(int nOccurrences) {
		this.nOccurrences = nOccurrences;
	}

	void setConfidence(float confidence) {
		this.confidence = confidence;
	}

	void setSaHead(TermId saHead) {
		this.saHead = saHead;
	}

	void setSaHead(String saHead) {
		setSaHead(new TermId(saHead));
	}

	void setSaModifier(TermId saModifier) {
		this.saModifier = saModifier;
	}

	void setSaModifier(String saModifier) {
		setSaModifier(new TermId(saModifier));
	}

	void setSaPosition(ModifierPosition saPosition) {
		this.saPosition = saPosition;
	}

	void setSaPrep(String saPrep) {
		this.saPrep = saPrep;
	}
	
	@Override
	public void resolveTermIds(Map<String,TermCandidate> candidatesCollection) {
		head.resolveTermIds(candidatesCollection);
		if (saHead != null) {
			saHead.resolveTermIds(candidatesCollection);
		}
		if (saModifier != null) {
			saModifier.resolveTermIds(candidatesCollection);
		}
	}

	@Override
	public String toString() {
		return "CAND:" + id + " (" + form + ')';
	}
}
