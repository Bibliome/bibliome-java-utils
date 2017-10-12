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

package fr.inra.maiage.bibliome.util.genia;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A genia event.
 * @author rbossy
 *
 */
public class Event {
	private final String id;
	private final String type;
	private final boolean input;
	private final Map<String,Entity> args = new HashMap<String,Entity>();
	
	/**
	 * Creates a genia event.
	 * @param id
	 * @param type
	 * @param input
	 */
	public Event(String id, String type, boolean input) {
		super();
		this.id = id;
		this.type = type;
		this.input = input;
	}

	/**
	 * Returns this event identifier.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns this event type.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Returns the roles of all arguments of this event.
	 */
	public Collection<String> getRoles() {
		return Collections.unmodifiableCollection(args.keySet());
	}
	
	/**
	 * Returns the argument of this event with the specified role.
	 * @param role
	 */
	public Entity getArg(String role) {
		if (args.containsKey(role))
			return args.get(role);
		throw new RuntimeException(id + " has no arg with role " + role);
	}
	
	/**
	 * Returns either this event has an argument with the specified role.
	 * @param role
	 */
	public boolean hasArg(String role) {
		return args.containsKey(role);
	}
	
	/**
	 * Sets the specified argument to this event.
	 * @param role
	 * @param arg
	 */
	void addArg(String role, Entity arg) {
		if (args.containsKey(role))
			throw new RuntimeException(id + " already has an arg with role " + role);
		if (input && !arg.isInput())
			throw new RuntimeException(id + " is input, not argument " + arg.getId());
		args.put(role, arg);
	}
	
	/**
	 * Returns either this event was read from a .a1 file.
	 */
	public boolean isInput() {
		return input;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(id);
		sb.append("] ");
		sb.append(type);
		for (Map.Entry<String,Entity> e : args.entrySet()) {
			sb.append(' ');
			sb.append(e.getKey());
			sb.append(":(");
			sb.append(e.getValue());
			sb.append(')');
		}
		return sb.toString();
	}
}
