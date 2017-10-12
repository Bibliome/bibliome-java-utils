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

public enum FragmentTagType {
	CLOSE {
		@Override
		public int getPosition(Fragment frag) {
			return frag.getEnd();
		}

		@Override
		public int getOtherPosition(Fragment frag) {
			return frag.getStart();
		}

		@Override
		int compareFragments(int comp) {
			return -comp;
		}
	},
	
	EMPTY {
		@Override
		public int getPosition(Fragment frag) {
			return frag.getStart();
		}

		@Override
		public int getOtherPosition(Fragment frag) {
			return frag.getStart();
		}

		@Override
		int compareFragments(int comp) {
			return comp;
		}
	},
	
	OPEN {
		@Override
		public int getPosition(Fragment frag) {
			return frag.getStart();
		}

		@Override
		public int getOtherPosition(Fragment frag) {
			return frag.getEnd();
		}

		@Override
		int compareFragments(int comp) {
			return comp;
		}
	};

	public abstract int getPosition(Fragment frag);
	public abstract int getOtherPosition(Fragment frag);
	abstract int compareFragments(int comp);
}
