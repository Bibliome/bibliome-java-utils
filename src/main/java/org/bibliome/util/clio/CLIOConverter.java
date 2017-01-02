package org.bibliome.util.clio;

/**
 * A CLIO converter converts command line arguments into a given type.
 * @author rbossy
 *
 */
public interface CLIOConverter {
	/**
	 * Types this converter supports.
	 */
	Class<?>[] targetTypes();
	
	/**
	 * Converts the specified string.
	 * @param arg
	 * @throws CLIOConversionException
	 */
	Object convert(String arg) throws CLIOConversionException;
}
