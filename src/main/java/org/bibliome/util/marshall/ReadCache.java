package org.bibliome.util.marshall;

/**
 * Unmarshaller read-cache.
 * @author rbossy
 *
 * @param <T>
 */
public interface ReadCache<T> {
	/**
	 * Returns the object at the specified position, null if there is no object currently retained at the specified position in this cache.
	 * @param position
	 */
	T get(int position);
	
	/**
	 * Retains the specified object at the specified position.
	 * @param position
	 * @param object
	 */
	void put(int position, T object);
}
