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

package org.bibliome.util.bionlpst.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bibliome.util.Strings;
import org.bibliome.util.bionlpst.BioNLPSTAnnotation;
import org.bibliome.util.bionlpst.BioNLPSTDocument;
import org.bibliome.util.bionlpst.BioNLPSTException;
import org.bibliome.util.bionlpst.Equivalence;
import org.bibliome.util.bionlpst.Sourced;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

public class DocumentSchema {
	private final Map<String,AnnotationSchema> annotationSchemas = new HashMap<String,AnnotationSchema>();
	private final Collection<String> equivalenceTypes = new HashSet<String>();
	private boolean homogeneousEquivalences = true;

	public DocumentSchema() {
		super();
	}
	
	public DocumentSchema(Element elt) throws BioNLPSTException {
		for (Element child : XMLUtils.childrenElements(elt)) {
			String tagName = child.getTagName();
			switch (tagName) {
			case "equivalences":
				homogeneousEquivalences = XMLUtils.getBooleanAttribute(child, "homogeneous", true);
				for (String type : Strings.splitAndTrim(child.getNodeValue(), ',', 0))
					addEquivalenceType(type);
				break;
			case "text-bound":
				int maxFragments = XMLUtils.getIntegerAttribute(child, "max-fragments", 1);
				new TextBoundSchema(this, getType(child), maxFragments);
				break;
			case "relation":
				RelationSchema relationSchema = new RelationSchema(this, getType(child));
				for (Element argElt : XMLUtils.childrenElements(child)) {
					String role = argElt.getTagName();
					List<String> types = Strings.splitAndTrim(argElt.getNodeValue(), ',', 0);
					boolean mandatory = XMLUtils.getBooleanAttribute(argElt, "mandatory", true);
					relationSchema.addArgumentSchema(role, types, mandatory);
				}
				break;
			case "event":
				EventSchema eventSchema = new EventSchema(this, getType(child));
				for (Element argElt : XMLUtils.childrenElements(child)) {
					String role = argElt.getTagName();
					List<String> types = Strings.splitAndTrim(argElt.getNodeValue(), ',', 0);
					if (role.equals("trigger")) {
						for (String type : types)
							eventSchema.addTriggerType(type);
					}
					else {
						boolean mandatory = XMLUtils.getBooleanAttribute(argElt, "mandatory", true);
						eventSchema.addArgumentSchema(role, types, mandatory);
					}
				}
				break;
			case "modification":
				ModificationSchema modificationSchema = new ModificationSchema(this, getType(child));
				for (String type : Strings.splitAndTrim(child.getNodeValue(), ',', 0))
					modificationSchema.addAnnotationType(type);
				break;
			case "normalization":
				String regex = XMLUtils.getAttribute(child, "referent", ".*");
				NormalizationSchema normalizationSchema = new NormalizationSchema(this, getType(child), Pattern.compile(regex));
				for (String type : Strings.splitAndTrim(child.getNodeValue(), ',', 0))
					normalizationSchema.addAnnotationType(type);
				break;
			default:
				throw new BioNLPSTException("cannot understand element " + tagName);
			}
		}
		checkSchema();
	}
	
	private static String getType(Element elt) throws BioNLPSTException {
		if (elt.hasAttribute("type"))
			return elt.getAttribute("type").trim();
		throw new BioNLPSTException("missing type attribute");
	}

	public boolean isHomogeneousEquivalences() {
		return homogeneousEquivalences;
	}

	public void addAnnotationSchema(AnnotationSchema annotationSchema) throws BioNLPSTException {
		String type = annotationSchema.getType();
		if (annotationSchemas.containsKey(type))
			throw new BioNLPSTException("duplicate schema for type: " + type);
		annotationSchemas.put(type, annotationSchema);
	}
	
	public void addEquivalenceType(String type) {
		equivalenceTypes.add(type);
	}
	
	public void checkSchema() throws BioNLPSTException {
		Collection<String> knownTypes = annotationSchemas.keySet();
		for (AnnotationSchema schema : annotationSchemas.values()) {
			Collection<String> referencedTypes = schema.getReferencedTypes();
			checkReferencedTypes(knownTypes, referencedTypes, schema.getType());
		}
		checkReferencedTypes(knownTypes, new HashSet<String>(equivalenceTypes), "equivalence");
	}
	
	private static void checkReferencedTypes(Collection<String> knownTypes, Collection<String> referencedTypes, String type) throws BioNLPSTException {
		referencedTypes.removeAll(knownTypes);
		if (!referencedTypes.isEmpty()) {
			String msg = "schema for " + type + " references unknown types " + Strings.join(referencedTypes, ", ");
			throw new BioNLPSTException(msg);
		}

	}

	public void check(Collection<String> messages, BioNLPSTAnnotation annotation) {
		String type = annotation.getType();
		if (annotationSchemas.containsKey(type)) {
			AnnotationSchema schema = annotationSchemas.get(type);
			schema.check(messages, annotation);
		}
		else {
			String msg = annotation.message("undefined type " + type);
			messages.add(msg);
		}
	}
	
	public void check(Collection<String> messages, Equivalence equiv) {
		if (!equivalenceTypes.isEmpty()) {
			for (BioNLPSTAnnotation annotation : equiv.getAnnotations()) {
				checkType(messages, equiv, "equivalence item", equivalenceTypes, annotation);
			}
		}
		if (homogeneousEquivalences) {
			BioNLPSTAnnotation first = null;
			for (BioNLPSTAnnotation annotation : equiv.getAnnotations()) {
				if (first == null) {
					first = annotation;
				}
				else {
					String expectedType = first.getType();
					String type = annotation.getType();
					if (!type.equals(expectedType)) {
						String msg = equiv.message("different types in equivalence, see " + first.message() + " and " + annotation.message());
						messages.add(msg);
					}
				}
			}
		}
	}
	
	static void checkType(Collection<String> messages, Sourced sourced, String subject, Collection<String> types, BioNLPSTAnnotation ref) {
		String type = ref.getType();
		if (!types.contains(type)) {
			String msg = sourced.message(subject + " must be one of (" + Strings.join(types, ", ") + "), see " + ref.message());
			messages.add(msg);
		}
	}

	public Collection<String> check(BioNLPSTDocument doc) {
		Collection<String> result = new ArrayList<String>();
		for (BioNLPSTAnnotation annotation : doc.getAnnotations()) {
			check(result, annotation);
		}
		for (Equivalence equiv : doc.getEquivalences()) {
			check(result, equiv);
		}
		return result;
	}
	
	public Collection<AnnotationSchema> getAnnotationSchemas() {
		return Collections.unmodifiableCollection(annotationSchemas.values());
	}
}
