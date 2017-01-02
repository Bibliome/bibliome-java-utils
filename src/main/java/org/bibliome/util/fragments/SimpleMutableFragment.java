package org.bibliome.util.fragments;

public class SimpleMutableFragment implements Fragment {
	private int start;
	private int end;
	
	public SimpleMutableFragment(int start, int end) {
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

	public void setStart(int start) {
		this.start = start;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	protected void toString(StringBuilder sb) {
		sb.append(start);
		sb.append('-');
		sb.append(end);
		sb.append(" [mutable]");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}
}
