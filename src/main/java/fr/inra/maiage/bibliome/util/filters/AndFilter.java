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

package fr.inra.maiage.bibliome.util.filters;

public class AndFilter<T> implements Filter<T> {
	private final Filter<T> left;
	private final Filter<T> right;
	
	public AndFilter(Filter<T> left, Filter<T> right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean accept(T x) {
		return left.accept(x) && right.accept(x);
	}
}
