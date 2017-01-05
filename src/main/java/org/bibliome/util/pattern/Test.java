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

package org.bibliome.util.pattern;

import java.util.ArrayList;
import java.util.List;

import org.bibliome.util.filters.ParamFilter;

public class Test {
	public static void main(String[] args) {
		Group<Integer,Void,ParamFilter<Integer,Void>> top = new Group<Integer,Void,ParamFilter<Integer,Void>>(Quantifier.DEFAULT);
		top.addChild(new LowerThan(5));
		top.addChild(new HigherThan(6));
		SequencePattern<Integer,Void,ParamFilter<Integer,Void>> pattern = new SequencePattern<Integer,Void,ParamFilter<Integer,Void>>(top);
		List<Integer> seq = new ArrayList<Integer>(args.length);
		for (String arg : args)
			seq.add(Integer.parseInt(arg));
		SequenceMatcher<Integer> m = pattern.getMatcher(seq, null);
		while (m.next()) {
			System.out.println("indexes = " + m.getStartIndex() + "-" + m.getEndIndex());
			System.out.println("match = " + m.getMatchedElements());
		}
	}

	private static final class LowerThan implements ParamFilter<Integer,Void> {
		private final int max;

		private LowerThan(int max) {
			super();
			this.max = max;
		}

		@Override
		public boolean accept(Integer x, Void param) {
			return (x != null) && (x < max);
		}
	}

	private static final class HigherThan implements ParamFilter<Integer,Void> {
		private final int min;

		private HigherThan(int min) {
			super();
			this.min = min;
		}

		@Override
		public boolean accept(Integer x, Void param) {
			return (x != null) && (x > min);
		}
	}
}
