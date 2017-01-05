/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.bibliome.util.genia;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Schema for a genia event argument.
 * @author rbossy
 *
 */
public class RoleSchema {
	private final Collection<String> allowedTypes = new HashSet<String>();
	private final String name;
	private final boolean mandatory;
	
	/**
	 * Creates a role schema.
	 * @param name
	 * @param mandatory
	 */
	public RoleSchema(String name, boolean mandatory) {
		super();
		this.name = name;
		this.mandatory = mandatory;
	}

	private boolean check(Logger logger, Entity entity) {
		if (allowedTypes.contains(entity.getType()))
			return true;
		logger.severe("role " + name + " does not allow entity of type " + entity.getType());
		return false;
	}
	
	/**
	 * Checks the validity of the specified event for this role.
	 * @param logger
	 * @param event
	 */
	public boolean check(Logger logger, Event event) {
		if (event.hasArg(name))
			return check(logger, event.getArg(name));
		if (mandatory) {
			logger.severe(event.getId() + " has no arg with role " + name);
			return false;
		}
		return true;
	}
	
	/**
	 * Add entity types allowed for this role.
	 * @param type
	 */
	public void addAllowedTypes(String... type) {
		allowedTypes.addAll(Arrays.asList(type));
	}
	
	/**
	 * Returns the role name.
	 */
	public String getName() {
		return name;
	}
}
