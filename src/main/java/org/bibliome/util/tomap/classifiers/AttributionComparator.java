package org.bibliome.util.tomap.classifiers;

import java.util.Comparator;

public enum AttributionComparator implements Comparator<Attribution> {
	ASCENDING {
		@Override
		public int compare(Attribution o1, Attribution o2) {
			return Double.compare(o1.getScore(), o2.getScore());
		}
	},
	
	DESCENDING {
		@Override
		public int compare(Attribution o1, Attribution o2) {
			return Double.compare(o2.getScore(), o1.getScore());
		}
	};
}