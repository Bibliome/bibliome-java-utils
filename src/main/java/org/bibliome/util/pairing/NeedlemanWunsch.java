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

package org.bibliome.util.pairing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.bibliome.util.Pair;

/**
 * Implementation of Needleman-Wunsch global alignment.
 * @author rbossy
 *
 * @param <A>
 * @param <B>
 */
public class NeedlemanWunsch<A,B> {
	private final Pair<List<A>,List<B>> labels;
	private final AlignmentScore<A,B> score;
	private final int aLen;
	private final int bLen;
	private final Alignment[][] alignments;
	private final double[][] scores;
	
	/**
	 * Creates a new Needleman-Wunsch object for the specified sequences and alignment score.
	 * @param a
	 * @param b
	 * @param score
	 */
	public NeedlemanWunsch(List<A> a,List<B> b, AlignmentScore<A,B> score) {
		this.labels = new Pair<List<A>,List<B>>(a, b);
		this.score = score;
		aLen = a.size() + 1;
		bLen = b.size() + 1;
		alignments = new Alignment[aLen][bLen];
		scores = new double[aLen][bLen];
	}
	
	private enum Alignment {
		MATCH {
			@Override
			double getPrevious(double[][] scores, int i, int j) {
				return scores[i-1][j-1];
			}

			@Override
			<A, B> double getScore(AlignmentScore<A, B> score, A a, B b) {
				return score.getScore(a, b);
			}

			@Override
			void move(MutableCell cell) {
				cell.setPosition(cell.getRow() - 1, cell.getColumn() - 1);
			}

			@Override
			<A, B> A getFirst(Pair<List<A>,List<B>> labels, Cell cell) {
				return labels.first.get(cell.getRow()-1);
			}

			@Override
			<A, B> B getSecond(Pair<List<A>,List<B>> labels, Cell cell) {
				return labels.second.get(cell.getColumn()-1);
			}
		},
		
		INSERT {
			@Override
			double getPrevious(double[][] scores, int i, int j) {
				return scores[i-1][j];
			}

			@Override
			<A, B> double getScore(AlignmentScore<A, B> score, A a, B b) {
				return score.getGap();
			}

			@Override
			void move(MutableCell cell) {
				cell.setRow(cell.getRow()-1);
			}

			@Override
			<A, B> A getFirst(Pair<List<A>,List<B>> labels, Cell cell) {
				return labels.first.get(cell.getRow()-1);
			}

			@Override
			<A, B> B getSecond(Pair<List<A>,List<B>> labels, Cell cell) {
				return null;
			}
		},
		
		DELETE {
			@Override
			double getPrevious(double[][] scores, int i, int j) {
				return scores[i][j-1];
			}

			@Override
			<A, B> double getScore(AlignmentScore<A, B> score, A a, B b) {
				return score.getGap();
			}

			@Override
			void move(MutableCell cell) {
				cell.setColumn(cell.getColumn()-1);
			}

			@Override
			<A, B> A getFirst(Pair<List<A>,List<B>> labels, Cell cell) {
				return null;
			}

			@Override
			<A, B> B getSecond(Pair<List<A>,List<B>> labels, Cell cell) {
				return labels.second.get(cell.getColumn()-1);
			}
		};
		
		abstract double getPrevious(double[][] scores, int i, int j);
		abstract <A,B> double getScore(AlignmentScore<A,B> score, A a, B b);
		abstract void move(MutableCell cell);
		abstract <A,B> A getFirst(Pair<List<A>,List<B>> labels, Cell cell);
		abstract <A,B> B getSecond(Pair<List<A>,List<B>> labels, Cell cell);
	}

	private void init() {
		alignments[0][0] = null;
		scores[0][0] = score.initialScore();
		for (int i = 1; i < aLen; ++i) {
			alignments[i][0] = Alignment.INSERT;
			scores[i][0] = score.compute(i, score.getGap());
		}
		for (int j = 1; j < bLen; ++j) {
			alignments[0][j] = Alignment.DELETE;
			scores[0][j] = score.compute(j, score.getGap());
		}
	}
	
	/**
	 * Solve this alignment.
	 */
	public void solve() {
		init();
		Map<Alignment,Double> scoreMap = new EnumMap<Alignment,Double>(Alignment.class);
		for (int i = 1; i < aLen; ++i) {
			Alignment[] alignmentRow = alignments[i];
			double[] scoreRow = scores[i];
			A a = labels.first.get(i-1);
			for (int j = 1; j < bLen; ++j) {
				B b = labels.second.get(j-1);
				buildScoreMap(scoreMap, i, j, a, b);
				Alignment best = getBestAlignment(scoreMap);
				alignmentRow[j] = best;
				scoreRow[j] = score.combine(scoreMap.values(), scoreMap.get(best));
			}
		}
	}
	
	/**
	 * Returns the optimal alignment score.
	 * solve() must have been called previously, otherwise the return value is unspecified.
	 */
	public double getScore() {
		return scores[aLen-1][bLen-1];
	}
	
	/**
	 * Returns the optimal alignement.
	 * solve() must have been called previously, otherwise the return value is unspecified.
	 */
	public List<Pair<A,B>> getOptimalAlignment() {
		List<Pair<A,B>> result = new ArrayList<Pair<A,B>>();
		MutableCell current = new MutableCell(aLen-1, bLen-1);
		while (true) {
			Alignment al = alignments[current.getRow()][current.getColumn()];
			if (al == null)
				break;
			A a = al.getFirst(labels, current);
			B b = al.getSecond(labels, current);
			result.add(new Pair<A,B>(a, b));
			al.move(current);
		}
		Collections.reverse(result);
		return result;
	}
	
	private void buildScoreMap(Map<Alignment,Double> scoreMap, int i, int j, A a, B b) {
		for (Alignment al : Alignment.values()) {
			double prev = al.getPrevious(scores, i, j);
			double sc = al.getScore(score, a, b);
			scoreMap.put(al, score.compute(prev, sc));
		}
	}

	private static Alignment getBestAlignment(Map<Alignment,Double> map) {
		Map.Entry<Alignment,Double> best = null;
		for (Map.Entry<Alignment,Double> e : map.entrySet()) {
			if ((best == null) || (e.getValue() > best.getValue()))
				best = e;
		}
		return best.getKey();
	}
}
