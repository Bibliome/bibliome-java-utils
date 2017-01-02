package org.bibliome.util.yatea.pattern;

import java.util.Collection;

import org.bibliome.util.filters.ParamFilter;
import org.bibliome.util.pattern.Alternatives;
import org.bibliome.util.pattern.CapturingGroup;
import org.bibliome.util.pattern.Clause;
import org.bibliome.util.pattern.Group;
import org.bibliome.util.pattern.Predicate;
import org.bibliome.util.pattern.Quantifier;
import org.bibliome.util.pattern.SequencePattern;

public abstract class ChunkSequencePatternMapper<T,P,F extends ParamFilter<T,P>> {
	private class Visitor implements SubtermVisitor<Clause<T,P,F>,String>  {
		@Override
		public CapturingGroup<T,P,F> visit(Chunk chunk, String param) {
			CapturingGroup<T,P,F> result = new CapturingGroup<T,P,F>(Quantifier.DEFAULT, param);
			int modifiersBefore = 0;
			for (Subterm subterm : chunk.getSubterms()) {
				String next;
				switch (subterm.getRole()) {
					case HEAD:
						next = param + "H1";
						break;
					case MODIFIER:
						next = param + 'M' + (++modifiersBefore);
						break;
					default:
						next = param;
						break;
				}
				Clause<T,P,F> clause = subterm.accept(this, next);
				result.addChild(clause);
			}
			return result;
		}

		@Override
		public Clause<T,P,F> visit(Word word, String param) {
			F filter = getFilter(word);
			Predicate<T,P,F> pred =  new Predicate<T,P,F>(Quantifier.DEFAULT, filter);
			if (word.getRole() == SubtermRole.NONE) {
				return pred;
			}
			CapturingGroup<T,P,F> group = new CapturingGroup<T,P,F>(Quantifier.DEFAULT, param);
			group.addChild(pred);
			return group;
		}
	}
	
	private F getFilter(Word word) {
		String value = word.getValue();
		switch (word.getAttribute()) {
			case LEMMA: return getLemmaFilter(value);
			case POS: return getPOSFilter(value);
		}
		throw new RuntimeException();
	}
	
	protected abstract F getLemmaFilter(String value);
	protected abstract F getPOSFilter(String value);

	public SequencePattern<T,P,F> getSequencePattern(ParsingPattern parsingPattern, String prefix) {
		Group<T,P,F> group = new Visitor().visit(parsingPattern.getChunk(), prefix);
		return new SequencePattern<T,P,F>(group);
	}
	
	public SequencePattern<T,P,F> getSequencePattern(Collection<ParsingPattern> parsingPatterns) {
		Visitor visitor = new Visitor();
		Alternatives<T,P,F> alt = new Alternatives<T,P,F>();
		for (ParsingPattern pp : parsingPatterns) {
			String prefix = pp.getSource() + ':' + pp.getLineno() + ':';
			CapturingGroup<T,P,F> clause = visitor.visit(pp.getChunk(), prefix);
			alt.add(clause);
		}
		Group<T,P,F> group = new Group<T,P,F>(Quantifier.DEFAULT);
		group.addChild(alt);
		return new SequencePattern<T,P,F>(group);
	}
}
