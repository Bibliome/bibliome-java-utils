package org.bibliome.util.yatea.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Chunk extends Subterm {
	private final List<Subterm> subterms = new ArrayList<Subterm>(5);
	private Subterm head;
	private final List<Subterm> modifiers = new ArrayList<Subterm>(2);
	
	public void addSubterm(Subterm subterm) {
		switch (subterm.getRole()) {
			case HEAD:
				if (head != null) {
					throw new RuntimeException();
				}
				head = subterm;
				break;
			case MODIFIER:
				modifiers.add(subterm);
				break;
			case NONE:
				break;
		}
		subterms.add(subterm);
	}

	public List<Subterm> getSubterms() {
		return Collections.unmodifiableList(subterms);
	}

	public Subterm getHead() {
		return head;
	}

	public List<Subterm> getModifiers() {
		return Collections.unmodifiableList(modifiers);
	}

	@Override
	public <R,P> R accept(SubtermVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}
}