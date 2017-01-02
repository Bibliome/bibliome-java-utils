package org.bibliome.util.fragments;

public class SimpleFragment implements Fragment {
	private final int start;
	private final int end;
	
	public SimpleFragment(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int getEnd() {
		return end;
	}
	
	public int getLength() {
		return end - start;
	}
	
	protected void toString(StringBuilder sb) {
		sb.append(start);
		sb.append('-');
		sb.append(end);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}
}