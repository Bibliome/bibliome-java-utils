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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Schema for a genia event type.
 * @author rbossy
 *
 */
public class EventSchema {
	private final String type;
	private final Map<String,RoleSchema> roles = new HashMap<String,RoleSchema>();
	
	/**
	 * Creates an event type schema.
	 * @param type
	 */
	public EventSchema(String type) {
		super();
		this.type = type;
	}

	/**
	 * Returns the type validated by this schema.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Checks the validity of the specified event.
	 * @param logger
	 * @param event
	 */
	public boolean check(Logger logger, Event event) {
		boolean result = true;
		for (String role : event.getRoles())
			if (!roles.containsKey(role)) {
				logger.severe(event.getId() + " (" + type + ") does not allow the role " + role);
				result = false;
			}
		for (RoleSchema roleSchema : roles.values())
			result = roleSchema.check(logger, event) && result;
		return result;
	}
	
	/**
	 * Returns the roles supported by valid events.
	 */
	public Collection<RoleSchema> getRoles() {
		return Collections.unmodifiableCollection(roles.values());
	}
	
	/**
	 * Returns a schema for arguments with the specified role.
	 * @param name
	 */
	public RoleSchema getRole(String name) {
		return roles.get(name);
	}
	
	/**
	 * Adds a role supported by this event type.
	 * @param role
	 */
	public void addRole(RoleSchema role) {
		if (roles.containsKey(role.getName()))
			throw new RuntimeException("duplicate role definition for " + role.getName());
		roles.put(role.getName(), role);
	}
}