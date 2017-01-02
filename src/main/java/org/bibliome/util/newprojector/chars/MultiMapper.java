package org.bibliome.util.newprojector.chars;

import java.util.ArrayList;
import java.util.List;

import org.bibliome.util.newprojector.CharMapper;

class MultiMapper implements CharMapper {
	private final List<CharMapper> charMappers = new ArrayList<CharMapper>(2);
	
	MultiMapper(CharMapper cm1, CharMapper cm2) {
		charMappers.add(cm1);
		charMappers.add(cm2);
	}
	
	@Override
	public char map(int last, char c) {
		for (CharMapper cm : charMappers)
			c = cm.map(last, c);
		return c;
	}

	@Override
	public CharMapper combine(CharMapper cm) {
		charMappers.add(cm);
		return this;
	}
}
