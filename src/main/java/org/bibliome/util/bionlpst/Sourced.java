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

public class Sourced {
	private final String source;
	private final int lineno;
	private final BioNLPSTDocument document;
	private final Visibility visibility;

	public Sourced(String source, int lineno, BioNLPSTDocument document, Visibility visibility) {
		super();
		this.source = source;
		this.lineno = lineno;
		this.document = document;
		this.visibility = visibility;
	}

	public String getSource() {
		return source;
	}

	public int getLineno() {
		return lineno;
	}

	public BioNLPSTDocument getDocument() {
		return document;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public String message(String msg) {
		if (msg == null)
			return source + ':' + lineno;
		return source + ':' + lineno + ": " + msg;
	}
	
	public String message() {
		return message(null);
	}
	
	protected <T> T error(String msg) throws BioNLPSTException {
		throw new BioNLPSTException(message(msg));
	}
	
	protected BioNLPSTAnnotation resolveId(String id) throws BioNLPSTException {
		BioNLPSTAnnotation result = document.resolveId(this, id);
		if (visibility == Visibility.A1 && result.getVisibility() != Visibility.A1)
			error("reference from a1 to a2: " + id + " (" + result.message() + ")");
		return result;
	}
}
