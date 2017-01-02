package org.bibliome.util.taxonomy.reject;

import org.bibliome.util.Pair;
import org.bibliome.util.taxonomy.Name;

/**
 * Name reject that reject names rejected by both components.
 * @author rbossy
 *
 */
public class RejectConjunction extends Pair<RejectName,RejectName> implements RejectName {
	/**
	 * Creates a name reject that rejects names rejected by both specified components.
	 * @param first
	 * @param second
	 */
	public RejectConjunction(RejectName first, RejectName second) {
		super(first, second);
	}

	@Override
	public boolean reject(int taxid, Name name) {
		return first.reject(taxid, name) && second.reject(taxid, name);
	}
}
