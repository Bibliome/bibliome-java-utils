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
