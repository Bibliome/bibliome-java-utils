package org.bibliome.util.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.filters.ParamFilter;
import org.bibliome.util.mappers.Mapper;

/**
 * Object sequence pattern.
 * @author rbossy
 *
 * @param <T>
 */
public class SequencePattern<T,P,F extends ParamFilter<T,P>> {
	private final Group<T,P,F> top;
	private final List<F> filters;
	private final List<CapturingGroup<T,P,F>> capturingGroups;
	private final Pattern pattern;

	/**
	 * Creates a pattern with the specified top group.
	 * @param top
	 */
	public SequencePattern(Group<T,P,F> top) {
		super();
		this.top = top;
		Collection<F> filterSet = new HashSet<F>();
		top.collectFilters(filterSet);
		filters = new ArrayList<F>(filterSet);
		capturingGroups = new ArrayList<CapturingGroup<T,P,F>>();
		top.collectCapturingGroups(capturingGroups);
		StringBuilder sb = new StringBuilder();
//		System.err.println("filters = " + filters);
		top.toRegexp(sb, filters);
//		System.err.println("pattern: " + sb);
		pattern = Pattern.compile(sb.toString());
	}

	public Group<T,P,F> getTop() {
		return top;
	}

	/**
	 * Return the filters included in the top clause.
	 */
	public List<F> getFilters() {
		return Collections.unmodifiableList(filters);
	}
	
	private CharSequence toCharSequence(List<T> sequence, P param) {
		StringBuilder result = new StringBuilder();
		for (T x : sequence) {
			result.append(SequenceChars.TOKEN);
			for (ParamFilter<T,P> f : filters)
				result.append(f.accept(x, param) ? SequenceChars.TRUE : SequenceChars.FALSE);
		}
//		System.err.println(result);
		return result;
	}
	
	/**
	 * Returns a matcher object for the specified object sequence.
	 * @param sequence
	 */
	public SequenceMatcher<T> getMatcher(List<T> sequence, P param) {
		Matcher m = pattern.matcher(toCharSequence(sequence, param));
		return new SequenceMatcher<T>(filters.size() + 1, sequence, m);
	}

	/**
	 * Returns all capturing groups in this pattern in opening order.
	 */
	public List<CapturingGroup<T,P,F>> getCapturingGroups() {
		return Collections.unmodifiableList(capturingGroups);
	}
	
	public <T2,P2,F2 extends ParamFilter<T2,P2>> SequencePattern<T2,P2,F2> copy(Mapper<F,F2> mapper) {
		return new SequencePattern<T2,P2,F2>(top.copy(mapper));
	}
}
