package org.bibliome.util.taxonomy.reject;

import org.bibliome.util.taxonomy.Name;

public class RejectNone implements RejectName {
	public static final RejectNone INSTANCE = new RejectNone();

	private RejectNone() {
		super();
	}

	@Override
	public boolean reject(int taxid, Name name) {
		return false;
	}
}
