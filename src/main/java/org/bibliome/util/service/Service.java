package org.bibliome.util.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class annotation indicating the target class is a service implementation.
 * @author rbossy
 *
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Service {
	/**
	 * Service interface implemented by the class.
	 */
	Class<?> value();
}
