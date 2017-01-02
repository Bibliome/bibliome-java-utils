package org.bibliome.util.taxonomy.reject;

import java.util.Collection;

import org.bibliome.util.taxonomy.Name;

public class RejectDisjunction implements RejectName {
	private final Collection<RejectName> rejects;

	public RejectDisjunction(Collection<RejectName> rejects) {
		super();
		this.rejects = rejects;
	}

	@Override
	public boolean reject(int taxid, Name name) {
		for (RejectName reject : rejects)
			if (reject.reject(taxid, name))
				return true;
		return false;
	}
}
