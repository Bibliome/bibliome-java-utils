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

package org.bibliome.util.biotopes2012;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bibliome.util.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class AlvisAECampaign {
	private final int id;
	private final String name;
	private final Map<Integer,AlvisAEDocument> docsByID = new HashMap<Integer,AlvisAEDocument>();
	private final Map<String,AlvisAEDocument> docsByDescription = new HashMap<String,AlvisAEDocument>();
	
	public AlvisAECampaign(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
//	private final String getSQL(String schema, int annotationType, boolean first) {
//		String select = "SELECT id, doc_id, text_annotations, groups, relations, type, user_id ";
//		String from = "FROM " + schema + ".annotationset ";
//		String where = "WHERE campaign_id = " + id + ' ';
//		switch (annotationType) {
//		case 1:
//		case 2:
//			where = where + "AND type = " + annotationType + ' ';
//		}
//		String order = "ORDER BY created";
//		if (first)
//			where = where + " AND head ";
//		String result = select + from + where + order;
//		System.out.println(result);
//		return result;
//	}
	
	private static String getDocDescriptionsAsSQL(Collection<String> docsDescriptions) {
		if (docsDescriptions == null)
			return "";
		if (docsDescriptions.isEmpty())
			return "";
		return " AND description IN ('" + Strings.join(docsDescriptions, "', '") + "')";
	}
	
	public void load(Connection connection, String schema, int annotationType, Collection<String> docDescriptions, boolean first) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet docRS = statement.executeQuery("SELECT description, contents, id, owner, props FROM " + schema + ".document, " + schema + ".campaigndocument WHERE doc_id = id AND campaign_id = " + id + getDocDescriptionsAsSQL(docDescriptions));
		String asSQL = "SELECT id, text_annotations, groups, relations, type, user_id FROM " + schema + ".annotationset WHERE campaign_id = " + id + " AND doc_id = ? AND type = " + annotationType + (first ? " " : " AND head ") + "ORDER BY created";
		//System.out.println(asSQL);
		PreparedStatement asStatement = connection.prepareStatement(asSQL);
		while (docRS.next()) {
			String descr = docRS.getString("description");
			if (!docDescriptions.contains(descr))
				continue;
			String contents = docRS.getString("contents");
			int docId = docRS.getInt("id");
			AlvisAEDocument doc = new AlvisAEDocument(this, docId, descr, contents);
			AlvisAEProperties props = doc.getProperties();
			props.loadJSON((JSONObject) JSONValue.parse(docRS.getString("props")));

			//System.out.println("docId = " + docId);
			asStatement.setInt(1, docId);
			ResultSet asRS = asStatement.executeQuery();
			if (!asRS.next())
				continue;
			int setId = asRS.getInt("id");
			int user = asRS.getInt("user_id");
			int type = asRS.getInt("type");
			AlvisAEAnnotationSet aset = new AlvisAEAnnotationSet(doc, setId, user, type);
			JSONArray texts = (JSONArray) JSONValue.parse(asRS.getString("text_annotations"));
			for (Object o : texts)
				new TextBound(aset, (JSONObject) o);
			JSONArray groups = (JSONArray) JSONValue.parse(asRS.getString("groups"));
			for (Object o : groups)
				new Group(aset, (JSONObject) o);
			JSONArray relations = (JSONArray) JSONValue.parse(asRS.getString("relations"));
			for (Object o : relations)
				new Relation(aset, (JSONObject) o);
			asRS.close();
		}
		docRS.close();
	}
	
//	public void load(Connection connection, String schema, int annotationType, Collection<String> docDescriptions, boolean first) throws SQLException {
//		PreparedStatement docStatement = connection.prepareStatement("SELECT description, contents, id, owner, props FROM " + schema + ".document WHERE id = ?" + getDocDescriptionsAsSQL(docDescriptions));
//		Statement statement = connection.createStatement();
//		ResultSet resultSet = statement.executeQuery(getSQL(schema, annotationType, first));
//		while (resultSet.next()) {
//			int docId = resultSet.getInt("doc_id");
//			AlvisAEDocument doc;
//			if (docsByID.containsKey(docId))
//				doc = docsByID.get(docId);
//			else {
//				docStatement.setInt(1, docId);
//				ResultSet docRS = docStatement.executeQuery();
//				if (!docRS.next())
//					continue;
//				String description = docRS.getString("description");
//				String contents = docRS.getString("contents");
//				doc = new AlvisAEDocument(this, id, description, contents);
//				AlvisAEProperties props = doc.getProperties();
//				props.loadJSON((JSONObject) JSONValue.parse(docRS.getString("props")));
//				docRS.close();
//			}
//			int setId = resultSet.getInt("id");
//			int user = resultSet.getInt("user_id");
//			int type = resultSet.getInt("type");
//			AlvisAEAnnotationSet aset = new AlvisAEAnnotationSet(doc, setId, user, type);
//			JSONArray texts = (JSONArray) JSONValue.parse(resultSet.getString("text_annotations"));
//			for (Object o : texts)
//				new TextBound(aset, (JSONObject) o);
//			JSONArray groups = (JSONArray) JSONValue.parse(resultSet.getString("groups"));
//			for (Object o : groups)
//				new Group(aset, (JSONObject) o);
//			JSONArray relations = (JSONArray) JSONValue.parse(resultSet.getString("relations"));
//			for (Object o : relations)
//				new Relation(aset, (JSONObject) o);
//		}
//		resultSet.close();
//	}

	void addDocument(AlvisAEDocument doc) {
		docsByID.put(doc.getId(), doc);
		docsByDescription.put(doc.getDescription(), doc);
	}

	public int getId() {
		return id;
	}
	
	public AlvisAEDocument getDocumentByDescription(String descr) {
		return docsByDescription.get(descr);
	}
	
	public Collection<AlvisAEDocument> getDocuments() {
		return Collections.unmodifiableCollection(docsByDescription.values());
	}

	public String getName() {
		return name;
	}
}
