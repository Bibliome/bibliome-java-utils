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

package fr.inra.maiage.bibliome.util.filelines;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class TabularLine extends ArrayList<String> {
	private final String source;
	private int lineno;
	
	public TabularLine(String source, int lineno) {
		super();
		this.source = source;
		this.lineno = lineno;
	}
	
	public TabularLine(String source) {
		this(source, 0);
	}
	
	public String getSource() {
		return source;
	}
	
	public int getLineno() {
		return lineno;
	}
	
	void incrLineno() {
		lineno++;
	}
}
