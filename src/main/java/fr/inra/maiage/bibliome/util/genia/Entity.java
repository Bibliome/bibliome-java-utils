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

/**
 * A genia entity.
 * @author rbossy
 *
 */
public class Entity {
	private final String id;
	private final String type;
	private final String form;
	private final int start;
	private final int end;
	private final boolean input;
	
	/**
	 * Creates an entity.
	 * @param id
	 * @param type
	 * @param form
	 * @param start
	 * @param end
	 * @param input
	 */
	public Entity(String id, String type, String form, int start, int end, boolean input) {
		super();
		this.id = id;
		this.type = type;
		this.form = form;
		this.start = start;
		this.end = end;
		this.input = input;
	}

	/**
	 * Returns this entity type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns this entity surface form.
	 */
	public String getForm() {
		return form;
	}

	/**
	 * Returns this entity start position.
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Returns this entity end position.
	 */
	public int getEnd() {
		return end;
	}
	
	/**
	 * Returns this entity identifier.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns either this entity was read from a .a1 file.
	 */
	public boolean isInput() {
		return input;
	}

	@Override
	public String toString() {
		return "[" + id + "] " + type + ":\"" + form + "\" " + start + '-' + end;
	}
}
