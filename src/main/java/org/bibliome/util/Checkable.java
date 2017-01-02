package org.bibliome.util;

import java.util.logging.Logger;

/**
 * Class of objects that may be checked.
 * @author rbossy
 *
 */
public interface Checkable {
	/**
	 * Checks this object.
	 * @param logger logger where warnings and errors are send
	 * @return true if this object constraints are met
	 */
	boolean check(Logger logger);
}
