package org.bibliome.util.filelines;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class TabularLine extends ArrayList<String> {
	private final String source;
	private int lineno;
	
	public TabularLine(String source, int lineno) {
		super();
		this.source = source;
		this.lineno = lineno;
	}
	
	public TabularLine(String source) {
		this(source, 0);
	}
	
	public String getSource() {
		return source;
	}
	
	public int getLineno() {
		return lineno;
	}
	
	void incrLineno() {
		lineno++;
	}
}
