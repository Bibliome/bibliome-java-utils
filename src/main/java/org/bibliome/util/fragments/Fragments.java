package org.bibliome.util.fragments;

import java.util.List;

public class Fragments {
	public static final boolean includes(Fragment out, Fragment in) {
		return out.getStart() <= in.getStart() && out.getEnd() >= in.getEnd();
	}
	
	public static final boolean overlaps(Fragment a, Fragment b) {
		if (b.getStart() < a.getStart()) {
			return overlaps(b, a);
		}
		return a.getEnd() > b.getStart();
	}
	
	public static boolean strictlyOverlaps(Fragment a, Fragment b) {
		return overlaps(a, b) && !(includes(a, b) || includes(b, a));
	}

	public static final <F extends Fragment> int searchStartLeft(List<F> fragments, int pos) {
        int lo = 0;
        int hi = fragments.size();
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            if (fragments.get(mid).getStart() < pos)
                lo = mid + 1;
            else
                hi = mid;
        }
        return lo;
    }

	public static final <F extends Fragment> int searchStartRight(List<F> fragments, int pos) {
        int lo = 0;
        int hi = fragments.size();
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            if (fragments.get(mid).getStart() > pos)
                hi = mid;
            else
                lo = mid + 1;
        }
        return lo;
    }
	
	public static final <T> List<T> subList(List<T> list, Fragment frag) {
		return list.subList(frag.getStart(), frag.getEnd());
	}
	
	public static final String substring(String s, Fragment frag) {
		return s.substring(frag.getStart(), frag.getEnd());
	}
}
