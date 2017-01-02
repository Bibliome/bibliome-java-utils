package org.bibliome.util.fragments;

public class DataFragment<T> extends SimpleFragment {
	private final T value;
	
	public DataFragment(int start, int end, T value) {
		super(start, end);
		this.value = value;
	}

	public T getValue() {
		return value;
	}
}
