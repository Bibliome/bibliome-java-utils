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

package org.bibliome.util.bionlpst;

public class Event extends AnnotationWithArgs {
	private final String triggerId;
	private BioNLPSTAnnotation trigger;
	
	public Event(String source, int lineno, BioNLPSTDocument document, Visibility visibility, String id, String type, String triggerId) throws BioNLPSTException {
		super(source, lineno, document, visibility, id, type);
		this.triggerId = triggerId;
	}

	public String getTriggerId() {
		return triggerId;
	}

	public BioNLPSTAnnotation getTrigger() {
		return trigger;
	}

	@Override
	public void resolveIds() throws BioNLPSTException {
		super.resolveIds();
		trigger = resolveId(triggerId);
	}

	@Override
	public <R,P> R accept(BioNLPSTAnnotationVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	@Override
	public AnnotationKind getKind() {
		return AnnotationKind.EVENT;
	}
}
