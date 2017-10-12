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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Schema for a genia corpus.
 * @author rbossy
 *
 */
public class Schema {
	private final Collection<String> entityTypes = new HashSet<String>();
	private final Map<String,EventSchema> eventTypes = new HashMap<String,EventSchema>();
	
	/**
	 * Checks the validity of the specified entity.
	 * @param logger
	 * @param entity
	 */
	public boolean check(Logger logger, Entity entity) {
		//logger.finer("checking " + entity.getId());
		if (entityTypes.contains(entity.getType()))
			return true;
		logger.severe(entity.getId() + " has unknown type " + entity.getType());
		return false;
	}
	
	/**
	 * Checks the validity of the specified event.
	 * @param logger
	 * @param event
	 */
	public boolean check(Logger logger, Event event) {
		//logger.finer("checking " + event.getId());
		if (eventTypes.containsKey(event.getType()))
			return eventTypes.get(event.getType()).check(logger, event);
		logger.severe(event.getId() + " has unknown type " + event.getType());
		return false;
	}
	
	/**
	 * Checks the validity of the specified document.
	 * @param logger
	 * @param doc
	 */
	public boolean check(Logger logger, Document doc) {
		logger.fine("checking " + doc.getId());
		boolean result = true;
		for (Entity entity : doc.getEntities())
			result = check(logger, entity) && result;
		for (Event event : doc.getEvents())
			result = check(logger, event) && result;
		return result;
	}
	
	/**
	 * Checks the validity of the specified corpus.
	 * @param logger
	 * @param corpus
	 */
	public Boolean check(Logger logger, Corpus corpus) {
		boolean result = true;
		for (Document doc : corpus.getDocuments())
			result = check(logger, doc) && result;
		return result;
	}
	
	/**
	 * Returns all accepted entity types.
	 */
	public Collection<String> getEntityTypes() {
		return Collections.unmodifiableCollection(entityTypes);
	}
	
	/**
	 * Adds the specified entity types.
	 * @param entityType
	 */
	public void addEntityTypes(String... entityType) {
		entityTypes.addAll(Arrays.asList(entityType));
	}
	
	/**
	 * Returns the event schema for the specified event type.
	 * @param eventType
	 */
	public EventSchema getEventSchema(String eventType) {
		return eventTypes.get(eventType);
	}
	
	/**
	 * Adds the specified event schema.
	 * @param schema
	 */
	public void addEventSchema(EventSchema schema) {
		String eventType = schema.getType();
		if (eventTypes.containsKey(eventType))
			throw new RuntimeException("duplicate event schema for " + eventType);
		eventTypes.put(eventType, schema);
	}
	
	private static final Pattern WORD_PATTERN = Pattern.compile("\\w+");

	/**
	 * Loads the schema in the specified propertiess object.
	 * @param props
	 */
	public void fromProperties(Properties props) {
		if (props.containsKey("entities")) {
			Matcher m = WORD_PATTERN.matcher(props.getProperty("entities"));
			while (m.find())
				addEntityTypes(m.group());
		}
		for (Object key : props.keySet()) {
			if (!(key instanceof String))
				continue;
			String sKey = (String) key;
			int dot = sKey.indexOf('.');
			if (dot < 0)
				continue;
			String eventType = sKey.substring(0, dot);
			EventSchema eventSchema;
			if (eventTypes.containsKey(eventType))
				eventSchema = eventTypes.get(eventType);
			else {
				eventSchema = new EventSchema(eventType);
				eventTypes.put(eventType, eventSchema);
			}
			String role = sKey.substring(dot + 1);
			RoleSchema roleSchema = eventSchema.getRole(role);
			if (roleSchema == null) {
				roleSchema = new RoleSchema(role, true);
				eventSchema.addRole(roleSchema);
			}
			Matcher m = WORD_PATTERN.matcher(props.getProperty(sKey));
			while (m.find())
				roleSchema.addAllowedTypes(m.group());
		}
	}
}
