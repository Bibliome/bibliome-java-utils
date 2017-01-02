package org.bibliome.util.yatea.pattern;

public abstract class Subterm {
	private SubtermRole role = SubtermRole.NONE;

	public SubtermRole getRole() {
		return role;
	}

	public void setRole(SubtermRole role) {
		if (this.role != SubtermRole.NONE) {
			throw new RuntimeException();
		}
		this.role = role;
	}
	
	public abstract <R,P> R accept(SubtermVisitor<R,P> visitor, P param);
}