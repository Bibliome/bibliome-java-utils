package org.bibliome.util.bionlpst;

public enum Visibility {
	A1(".a1"),
	A2(".a2"),
	PREDICTION(".a2");
	
	public final String fileSuffix;

	private Visibility(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}
}
