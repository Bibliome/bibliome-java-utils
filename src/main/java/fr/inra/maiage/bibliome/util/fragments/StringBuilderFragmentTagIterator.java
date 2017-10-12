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

package fr.inra.maiage.bibliome.util.fragments;

public abstract class StringBuilderFragmentTagIterator<F extends Fragment> implements FragmentTagIterator<String,F> {
	protected final StringBuilder result = new StringBuilder();
	
	@Override
	public void handleGap(String param, int from, int to) {
		result.append(escape(param.substring(from, to)));
	}

	@Override
	public void handleHead(String param, int to) {
		result.append(escape(param.substring(0, to)));
	}

	@Override
	public void handleTail(String param, int from) {
		result.append(escape(param.substring(from)));
	}
	
	protected abstract String escape(String s);
	
	public String getResult() {
		return result.toString();
	}
}
