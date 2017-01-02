package org.bibliome.util.newprojector;

/**
 * A match result.
 * @author rbossy
 *
 * @param <T>
 */
public final class Match<T> {
	private State<T> state = null;
	private int start = -1;
	private int end = -1;
	
	/**
	 * Returns the match state.
	 */
	public State<T> getState() {
		return state;
	}
	
	/**
	 * Returns the start position of this match in the target string.
	 */
	public int getStart() {
		return start;
	}
	
	/**
	 * Returns the end position of this match in the target string.
	 */
	public int getEnd() {
		return end;
	}
	
	void setState(State<T> state) {
		this.state = state;
	}
	
	void setStart(int start) {
		this.start = start;
	}
	
	void setEnd(int end) {
		this.end = end;
	}
	
	Match<T> copy() {
		Match<T> result = new Match<T>();
		result.setState(state);
		result.setStart(start);
		result.setEnd(end);
		return result;
	}
}
