package org.bibliome.util.genia;

/**
 * A genia entity.
 * @author rbossy
 *
 */
public class Entity {
	private final String id;
	private final String type;
	private final String form;
	private final int start;
	private final int end;
	private final boolean input;
	
	/**
	 * Creates an entity.
	 * @param id
	 * @param type
	 * @param form
	 * @param start
	 * @param end
	 * @param input
	 */
	public Entity(String id, String type, String form, int start, int end, boolean input) {
		super();
		this.id = id;
		this.type = type;
		this.form = form;
		this.start = start;
		this.end = end;
		this.input = input;
	}

	/**
	 * Returns this entity type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns this entity surface form.
	 */
	public String getForm() {
		return form;
	}

	/**
	 * Returns this entity start position.
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Returns this entity end position.
	 */
	public int getEnd() {
		return end;
	}
	
	/**
	 * Returns this entity identifier.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns either this entity was read from a .a1 file.
	 */
	public boolean isInput() {
		return input;
	}

	@Override
	public String toString() {
		return "[" + id + "] " + type + ":\"" + form + "\" " + start + '-' + end;
	}
}
