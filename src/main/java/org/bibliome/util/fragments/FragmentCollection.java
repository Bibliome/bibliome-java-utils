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

package org.bibliome.util.fragments;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FragmentCollection<F extends Fragment> extends AbstractCollection<F> {
	private final List<F> fragments;
	
	public FragmentCollection(List<F> annotations) {
		super();
		this.fragments = annotations;
	}
	
	public FragmentCollection() {
		this(new ArrayList<F>());
	}

	public F get(int i) {
		return fragments.get(i);
	}
	
	@Override
	public Iterator<F> iterator() {
		return fragments.iterator();
	}
	
	@Override
	public int size() {
		return fragments.size();
	}
	
	public int indexOf(Fragment f) {
        return Collections.binarySearch(fragments, f, new FragmentComparator<Fragment>());
	}

    @Override
	public boolean add(F annot) {
        if (fragments.isEmpty()) {
            fragments.add(annot);
            return true;
        }
        int i = indexOf(annot);
        if (i < 0) {
            i = -(i + 1);
        }
        fragments.add(i, annot);
        return true;
    }

    public boolean remove(F annot) {
        int i = indexOf(annot);
        if (i < 0)
        	return false;
        fragments.remove(i);
        return true;
    }

    public boolean hasOverlaps() {
    	int reach = 0;
    	for (F f : fragments) {
    		if (f.getStart() < reach)
    			return true;
    		if (f.getEnd() > reach)
    			reach = f.getEnd();
    	}
    	return false;
    }

    private int searchStartLeft(int pos) {
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

    private int searchStartRight(int pos) {
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
    
    public boolean someContained(int from, int to) {
    	for (int i = searchStartLeft(from); i < fragments.size(); ++i) {
    		Fragment f = fragments.get(i);
    		if (f.getStart() >= to) {
    			return false;
    		}
    		if (f.getEnd() <= to) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean someContained(Fragment f) {
    	return someContained(f.getStart(), f.getEnd());
    }

    public FragmentCollection<F> between(int from, int to) {
        int fromi = searchStartLeft(from);
        int toi = searchStartRight(to);
        List<F> list;
        if (hasOverlaps()) {
            list = new ArrayList<F>();
            for (F f : fragments.subList(fromi, toi))
                if (f.getEnd() <= to)
                    list.add(f);
        }
        else {
            if ((toi > 0) && (fragments.get(toi - 1).getEnd() > to))
                toi--;
            if (toi < fromi)
                list = Collections.emptyList();
            else
                list = fragments.subList(fromi, toi);
        }
        return new FragmentCollection<F>(list);
    }

    public FragmentCollection<F> between(Fragment annot) {
        return between(annot.getStart(), annot.getEnd());
    }

    public FragmentCollection<F> after(int from) {
        return new FragmentCollection<F>(fragments.subList(searchStartLeft(from), fragments.size()));
    }

    public FragmentCollection<F> before(int to) {
        int toi = searchStartRight(to);
        List<F> list;
        if (hasOverlaps()) {
            list = new ArrayList<F>();
            for (F f : fragments.subList(0, toi))
                if (f.getEnd() <= to)
                    list.add(f);
        }
        else {
            if ((toi > 0) && (fragments.get(toi - 1).getEnd() > to))
                toi--;
            list = fragments.subList(0, toi);
        }
        return new FragmentCollection<F>(list);
    }

    public FragmentCollection<F> overlapping(int from, int to) {
        int fromi = searchStartLeft(from);
        int toi = searchStartRight(to);
        List<F> list;
        if (hasOverlaps()) {
            list = new ArrayList<F>();
            for (F f : fragments.subList(0, fromi))
                if (f.getEnd() > from)
                    list.add(f);
            for (F f : fragments.subList(fromi, toi))
                list.add(f);
        }
        else {
            if ((fromi > 0) && (fragments.get(fromi - 1).getEnd() > from))
                fromi--;
            list = fragments.subList(fromi, toi);
        }
        return new FragmentCollection<F>(list);
    }

    public FragmentCollection<F> overlapping(F f) {
        return overlapping(f.getStart(), f.getEnd());
    }

    public boolean someContains(int from, int to) {
    	for (F f : fragments) {
    		if (f.getStart() > from) {
    			return false;
    		}
    		if (f.getEnd() >= to) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean someContains(Fragment f) {
    	return someContains(f.getStart(), f.getEnd());
    }
    
    public FragmentCollection<F> including(int from, int to) {
    	FragmentCollection<F> result = new FragmentCollection<F>();
    	int n = searchStartRight(from);
    	for (int i = 0; i < n; ++i) {
    		F f = fragments.get(i);
    		if (f.getEnd() >= to)
    			result.add(f);
    	}
    	return result;
    }

    public FragmentCollection<F> including(F f) {
    	return including(f.getStart(), f.getEnd());
    }

    public FragmentCollection<F> span(Fragment annot) {
        int i = indexOf(annot);
        List<F> list;
        if (i < 0)
            list = Collections.emptyList();
        else if (hasOverlaps()) {
            list = new ArrayList<F>();
            ListIterator<F> lit = fragments.listIterator(i);
            while (lit.hasNext()) {
                F f = lit.next();
                if (annot.getStart() != f.getStart() || annot.getEnd() != f.getEnd())
                    break;
                list.add(f);
            }
            lit = fragments.listIterator(i);
            while (lit.hasPrevious()) {
                F f = lit.previous();
                if (annot.getStart() != f.getStart() || annot.getEnd() != f.getEnd())
                    break;
                list.add(f);
            }
        }
        else
            list = fragments.subList(i, i + 1);
        return new FragmentCollection<F>(list);
    }

    public FragmentCollection<F> span(int from, int to) {
    	FragmentCollection<F> result = new FragmentCollection<F>();
    	for (int i = searchStartLeft(from); i < fragments.size(); ++i) {
    		F f = fragments.get(i);
    		if (f.getStart() > from)
    			break;
    		if (f.getEnd() == to)
    			result.add(f);
    	}
    	return result;
    }

	@Override
	public boolean isEmpty() {
		return fragments.isEmpty();
	}

	@Override
	public Object[] toArray() {
		return fragments.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return fragments.toArray(a);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return fragments.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return fragments.retainAll(c);
	}

	@Override
	public void clear() {
		fragments.clear();
	}
}
