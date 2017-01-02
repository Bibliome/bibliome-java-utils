package org.bibliome.util.trie;

import java.util.ArrayList;
import java.util.List;

import org.bibliome.util.marshall.MReference;

class ValuesList<T> extends ArrayList<T> implements MReference<List<T>> {
	private static final long serialVersionUID = 1L;

	public ValuesList() {
		super(2);
	}

	@Override
	public int getPosition() {
		return -2;
	}

	@Override
	public List<T> get() {
		return this;
	}
}
