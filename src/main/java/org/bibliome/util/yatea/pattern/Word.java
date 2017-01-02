package org.bibliome.util.yatea.pattern;

public final class Word extends Subterm {
	private final String value;
	private final WordAttribute attribute;
	
	public Word(String value, WordAttribute attribute) {
		super();
		this.value = value;
		this.attribute = attribute;
	}

	public Word(String value) {
		this(value, WordAttribute.get(value));
	}

	public String getValue() {
		return value;
	}

	public WordAttribute getAttribute() {
		return attribute;
	}

	@Override
	public <R,P> R accept(SubtermVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}
}
