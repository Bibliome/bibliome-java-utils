package org.bibliome.util.marshall;

/**
 * A marshall reference.
 * @author rbossy
 *
 * @param <T>
 */
public interface MReference<T> {
	/**
	 * Returns the position of the referenced object.
	 */
	int getPosition();
	
	/**
	 * Returns the referenced object.
	 */
	T get();
}
