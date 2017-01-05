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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.EquivalenceHashSets;
import org.bibliome.util.EquivalenceSets;
import org.bibliome.util.Strings;

/**
 * A genia document.
 * @author rbossy
 *
 */
public class Document {
	private final String id;
	private final Map<String,Entity> entities = new HashMap<String,Entity>();
	private final Map<String,Event> events = new HashMap<String,Event>();
	private final EquivalenceSets<Entity> entityEquivalence = new EquivalenceHashSets<Entity>();
	private final EquivalenceSets<Event> eventEquivalence = new EquivalenceHashSets<Event>();

	/**
	 * Creates a document with the specified identifier.
	 * @param id
	 */
	Document(String id) {
		super();
		this.id = id;
	}

	/**
	 * Adds the specified entity to this document.
	 * @param entity
	 */
	void addEntity(Entity entity) {
		if (entities.containsKey(entity.getId()))
			throw new RuntimeException("duplicate entity identifier " + entity.getId() + " in " + id);
		entities.put(entity.getId(), entity);
	}
	
	/**
	 * Adds the specified event to this document.
	 * @param event
	 */
	void addEvent(Event event) {
		if (events.containsKey(event.getId()))
			throw new RuntimeException("duplicate event identifier " + event.getId() + " in " + id);
		events.put(event.getId(), event);
	}
	
	/**
	 * Returns the entity with the specified identifier.
	 * @param id
	 * @throws RuntimeException if there is no entity with the specified identifier in this document
	 */
	public Entity getEntity(String id) {
		if (!entities.containsKey(id))
			throw new RuntimeException("no entity with id " + id + " in " + this.id);
		return entities.get(id);
	}

	/**
	 * Returns the event with the specified identifier
	 * @param id
	 * @throws RuntimeException if there is no event with the specified identifier
	 */
	public Event getEvent(String id) {
		if (!events.containsKey(id))
			throw new RuntimeException("no event with id " + id + " in " + this.id);
		return events.get(id);
	}

	/**
	 * Returns all entities in this document.
	 */
	public Collection<Entity> getEntities() {
		return Collections.unmodifiableCollection(entities.values());
	}
	
	/**
	 * Returns all events in this document.
	 */
	public Collection<Event> getEvents() {
		return Collections.unmodifiableCollection(events.values());
	}
	
	/**
	 * Returns the identifier of this document.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns a copy of this document with only the entities and events read from a file.
	 */
	public Document copyInput() {
		Document result = new Document(id);
		for (Entity entity : entities.values())
			if (entity.isInput())
				result.addEntity(entity);
		for (Event event : events.values())
			if (event.isInput())
				result.addEvent(event);
		return result;
	}

	/**
	 * Returns entity equivalence sets.
	 */
	public EquivalenceSets<Entity> getEntityEquivalence() {
		return entityEquivalence;
	}

	/**
	 * Returns event equivalence sets.
	 */
	public EquivalenceSets<Event> getEventEquivalence() {
		return eventEquivalence;
	}

	private static final Pattern EQUIV_PATTERN = Pattern.compile("\\*\tEquiv (([TE])\\d+) (\\2\\d+)");
	
	/**
	 * Parses a line in genia format.
	 * @param line
	 * @param input
	 */
	private void parseLine(String line, boolean input, boolean removeRW) {
		Matcher m = EQUIV_PATTERN.matcher(line);
		if (m.matches()) {
			if (m.group(2).charAt(0) == 'E')
				eventEquivalence.setEquivalent(events.get(m.group(1)), events.get(m.group(3)));
			else
				entityEquivalence.setEquivalent(entities.get(m.group(1)), entities.get(m.group(3)));
			return;
		}
		if (removeRW) {
			char firstChar = line.charAt(0);
			if ((firstChar == 'W') || (firstChar == 'R'))
				return;
		}
		List<String> columns = Strings.split(line, '\t', -1);
		String id = columns.get(0);
		switch (columns.size()) {
		case 2:
			columns = Arrays.asList(columns.get(1).split("\\s+"));
			Event event = new Event(id, columns.get(0), input);
			for (int i = 1; i < columns.size(); ++i) {
				int col = columns.get(i).lastIndexOf(':');
				String role = columns.get(i).substring(0, col);
				String argId = columns.get(i).substring(col + 1);
				Entity arg = getEntity(argId);
				event.addArg(role, arg);
			}
			addEvent(event);
			break;
		case 3:
			String form = columns.get(2);
			columns = Arrays.asList(columns.get(1).split("\\s+"));
			Entity entity = new Entity(id, columns.get(0), form, Integer.parseInt(columns.get(1)), Integer.parseInt(columns.get(2)), input);
			addEntity(entity);
			break;
		default:
			throw new RuntimeException("could not parse line in " + id);
		}
	}
	
	/**
	 * Parses the specified reader in genia format.
	 * @param r
	 * @param input
	 * @throws IOException
	 */
	private void parse(BufferedReader r, boolean input, boolean removeRW) throws IOException {
		while (true) {
			String line = r.readLine();
			if (line == null)
				break;
			parseLine(line, input, removeRW);
		}
	}
	
	/**
	 * Parses the specified file in genia format.
	 * @param file
	 * @param input
	 * @throws IOException
	 */
	public void parse(File file, boolean input, boolean removeRW) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		parse(r, input, removeRW);
		r.close();
	}
}

