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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

public class FragmentTag<F extends Fragment> {
	private final F fragment;
	private final FragmentTagType tagType;
	
	private FragmentTag(F fragment, FragmentTagType tagType) {
		super();
		this.fragment = fragment;
		this.tagType = tagType;
	}

	public F getFragment() {
		return fragment;
	}

	public FragmentTagType getTagType() {
		return tagType;
	}
	
	public int getPosition() {
		return tagType.getPosition(fragment);
	}
	
	public int getFragmentLength() {
		return fragment.getEnd() - fragment.getStart();
	}
	
	public static <F extends Fragment> void createTags(Collection<FragmentTag<F>> fragments, F frag) {
		final int start = frag.getStart();
		final int end = frag.getEnd();
		if (start == end)
			fragments.add(new FragmentTag<F>(frag, FragmentTagType.EMPTY));
		else {
			fragments.add(new FragmentTag<F>(frag, FragmentTagType.OPEN));
			fragments.add(new FragmentTag<F>(frag, FragmentTagType.CLOSE));
		}
	}
	
	public static <F extends Fragment> List<FragmentTag<F>> createTagList(Comparator<F> comparator, Iterable<F> fragments) {
		List<FragmentTag<F>> result = new ArrayList<FragmentTag<F>>();
		for (F frag : fragments)
			createTags(result, frag);
		Collections.sort(result, new FragmentTagComparator<F>(comparator));
//		for (FragmentTag<F> f : result) {
//			System.err.println("f = " + f.tagType + "/" + f.fragment);
//		}
		return result;
	}
	
	public static <F extends Fragment> List<FragmentTag<F>> createTagList(Iterable<F> fragments) {
		return createTagList(new StableComparator<F>(), fragments);
	}
	
	public static <F extends Fragment> NavigableSet<FragmentTag<F>> createTagSet(Comparator<F> comparator, Iterable<F> fragments) {
		NavigableSet<FragmentTag<F>> result = new TreeSet<FragmentTag<F>>(new FragmentTagComparator<F>(comparator));
		for (F frag : fragments)
			createTags(result, frag);
		return result;
	}
	
	public static <F extends Fragment> NavigableSet<FragmentTag<F>> createTagSet(Iterable<F> fragments) {
		return createTagSet(new StableComparator<F>(), fragments);
	}
	
	public static <P,F extends Fragment> void iterateTags(FragmentTagIterator<P,F> iterator, P param, Iterable<FragmentTag<F>> tags, int offset) {
		FragmentTag<F> last = null;
		int lastPosition = 0;
//		System.err.println("offset = " + offset);
		for (FragmentTag<F> tag : tags) {
			final int position = tag.getPosition() - offset;
//			System.err.println(tag.getTagType() + " " + tag.getPosition() + " " + tag.getFragmentLength() + " " + tag.getFragment());
//			System.err.println("  lastPosition = " + lastPosition);
//			System.err.println("  last = " + last);
//			System.err.println("  position = " + position);
			if (last == null)
				iterator.handleHead(param, position);
			else
				iterator.handleGap(param, lastPosition, position);
			iterator.handleTag(param, tag);
			last = tag;
			lastPosition = position;
		}
		iterator.handleTail(param, last == null ? 0 : last.getPosition() - offset);
	}
	
	public static <P,F extends Fragment> void iterateFragments(FragmentTagIterator<P,F> iterator, P param, Comparator<F> comparator, Iterable<F> fragments, int offset) {
		iterateTags(iterator, param, createTagList(comparator, fragments), offset);
	}
	
	public static <P,F extends Fragment> void iterateFragments(FragmentTagIterator<P,F> iterator, P param, Iterable<F> fragments, int offset) {
		iterateTags(iterator, param, createTagList(fragments), offset);
	}

	@Override
	public String toString() {
		return tagType.toString() + ": " + fragment.toString();
	}
}
