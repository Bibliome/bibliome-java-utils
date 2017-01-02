package org.bibliome.util.taxonomy;

/**
 * Taxon name.
 * @author rbossy
 *
 */
public class Name {
	/**
	 * Name of the taxon.
	 */
	public final String name;
	
	/**
	 * Type of the name.
	 */
	public final String type;
	
	/**
	 * Creates a taxon name with the specified name and type.
	 * @param name
	 * @param type
	 */
	public Name(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Name))
			return false;
		Name other = (Name) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
