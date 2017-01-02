package org.bibliome.util.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class annotation indicating the target class implements several services.
 * @author rbossy
 *
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Services {
	/**
	 * Service interfaces that the class implements.
	 */
	Class<?>[] value();
}
