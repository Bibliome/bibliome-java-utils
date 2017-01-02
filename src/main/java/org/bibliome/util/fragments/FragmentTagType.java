package org.bibliome.util.fragments;

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
