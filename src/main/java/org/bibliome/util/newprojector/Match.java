/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
