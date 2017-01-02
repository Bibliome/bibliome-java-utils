package org.bibliome.util.alvisae;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.bibliome.util.SQLSelectQueryBuilder;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;
import org.json.simple.parser.ParseException;

public class Campaign {
	static final String ASSIGNMENT_TABLE = "documentassignment";
	static final String FIELD_DOCUMENT_REFERENCE = "doc_id";
	static final String FIELD_CAMPAIGN_REFERENCE = "campaign_id";

	private final boolean oldModel;
	private final String schema;
	private final int id;
	private final Map<Integer,AlvisAEDocument> documents = new TreeMap<Integer,AlvisAEDocument>();
	
	public Campaign(boolean oldModel, String schema, int id) {
		super();
		this.oldModel = oldModel;
		this.schema = schema;
		this.id = id;
	}

	public AlvisAEDocument resolveDocument(int id) {
		if (documents.containsKey(id))
			return documents.get(id);
		throw new RuntimeException(Integer.toString(id));
	}
	
	public boolean isOldModel() {
		return oldModel;
	}

	void addDocument(AlvisAEDocument doc) {
		if (documents.containsKey(doc.getId()))
			throw new RuntimeException();
		documents.put(doc.getId(), doc);
	}

	public int getId() {
		return id;
	}
	
	public Collection<AlvisAEDocument> getDocuments() {
		return Collections.unmodifiableCollection(documents.values());
	}

	public String getSchema() {
		return schema;
	}

	private SQLSelectQueryBuilder createDocumentQuery(LoadOptions options) {
//		QueryBuilder qb = new QueryBuilder(schema);
		SQLSelectQueryBuilder result = new SQLSelectQueryBuilder(schema);

//		qb.addDistinct();
		result.setDistinct(true);
		result.addSelect(oldModel ? AlvisAEDocument.FIELDS_OLD_MODEL : AlvisAEDocument.FIELDS);
		if (options.isLoadContents())
			result.addSelect(AlvisAEDocument.FIELD_CONTENTS);
		
		result.addFrom(AlvisAEDocument.TABLE, Campaign.ASSIGNMENT_TABLE);
		result.addWhereClause(Campaign.ASSIGNMENT_TABLE, Campaign.FIELD_CAMPAIGN_REFERENCE, id);
		result.addCrossClause(AlvisAEDocument.TABLE, AlvisAEDocument.FIELD_ID, Campaign.ASSIGNMENT_TABLE, Campaign.FIELD_DOCUMENT_REFERENCE);

		if (options.hasDocIds())
			result.addInClauseInteger(AlvisAEDocument.TABLE, AlvisAEDocument.FIELD_ID, options.getDocIds());
		if (options.hasDocDescriptions())
			result.addInClauseString(AlvisAEDocument.TABLE, AlvisAEDocument.FIELD_DESCRIPTION, options.getDocDescriptions());
		if (options.hasDocExternalIds() && !oldModel)
			result.addInClauseString(AlvisAEDocument.TABLE, AlvisAEDocument.FIELD_EXTERNAL_ID, options.getDocExternalIds());

		return result;
	}
	
	public void loadDocuments(Logger logger, Connection connection, LoadOptions options) throws SQLException, ParseException {
		SQLSelectQueryBuilder query = createDocumentQuery(options);
		ResultSet rs = query.runQuery(connection, logger);
		while (rs.next())
			new AlvisAEDocument(this, rs, options.isLoadContents());
	}
	
	private SQLSelectQueryBuilder createCommonAnnotationSetQueryBuilder() {
//		QueryBuilder result = new QueryBuilder(schema);
		SQLSelectQueryBuilder result = new SQLSelectQueryBuilder(schema);

//		result.addDistinct();
		result.setDistinct(true);
		result.addSelect(oldModel ? AnnotationSet.FIELDS_OLD_MODEL : AnnotationSet.FIELDS);
		result.addSelect(AnnotationSet.TABLE_USER + "." + AnnotationSet.FIELD_USER_ID + " AS " + AnnotationSet.FIELD_USER_ID_ALIAS);
		result.addSelect(AnnotationSet.FIELD_USER_NAME);
		if (!oldModel) {
			result.addSelect(AnnotationSet.TABLE_TASK + "." + AnnotationSet.FIELD_TASK_ID + " AS " + AnnotationSet.FIELD_TASK_ID_ALIAS);
			result.addSelect(AnnotationSet.FIELD_TASK_NAME);
		}
		
		result.addFrom(AnnotationSet.TABLE);
		result.addFrom(AnnotationSet.TABLE_USER);
		if (!oldModel)
			result.addFrom(AnnotationSet.TABLE_TASK);

		return result;
	}
	
	private void loadDependencies(Logger logger, Connection connection, LoadOptions options, AnnotationSet aset) throws SQLException, ParseException {
		SQLSelectQueryBuilder query = createAnnotationSetDependencyQuery(aset.getId());
		ResultSet rs = query.runQuery(connection, logger);
		while (rs.next()) {
			AnnotationSet dependency = new AnnotationSet(this, rs, false);
			dependency.load(options, rs);
			aset.addDependency(dependency);
		}
	}
	
	private SQLSelectQueryBuilder createAnnotationSetDependencyQuery(int aset) {
//		QueryBuilder qb = createCommonAnnotationSetQueryBuilder();
		SQLSelectQueryBuilder result = createCommonAnnotationSetQueryBuilder();
		
		result.addFrom(AnnotationSet.TABLE_DEPENDENCY);
		
		result.addCrossClause(AnnotationSet.TABLE, AnnotationSet.FIELD_ID, AnnotationSet.TABLE_DEPENDENCY, AnnotationSet.FIELD_DEPENDENCY_SOURCE);
		result.addWhereClause(AnnotationSet.TABLE_DEPENDENCY, AnnotationSet.FIELD_DEPENDENCY_SELF, aset);
		result.addCrossClause(AnnotationSet.TABLE, AnnotationSet.FIELD_USER_REFERENCE, AnnotationSet.TABLE_USER, AnnotationSet.FIELD_USER_ID);
		if (!oldModel)
			result.addCrossClause(AnnotationSet.TABLE, AnnotationSet.FIELD_TASK_REFERENCE, AnnotationSet.TABLE_TASK, AnnotationSet.FIELD_TASK_ID);

		return result;
	}
	
	private SQLSelectQueryBuilder createAnnotationSetQuery(LoadOptions options, Collection<Integer> documentIds) {
//		QueryBuilder qb = createCommonAnnotationSetQueryBuilder();
		SQLSelectQueryBuilder result = createCommonAnnotationSetQueryBuilder();

		result.addWhereClause(AnnotationSet.TABLE, AnnotationSet.FIELD_CAMPAIGN_REFEERNCE, id);
		if (!documentIds.isEmpty())
			result.addInClauseInteger(AnnotationSet.TABLE, AnnotationSet.FIELD_DOCUMENT_REFERENCE, documentIds);
		if (options.isHead())
			result.addWhereClause(AnnotationSet.TABLE + "." + AnnotationSet.FIELD_HEAD);
		else
			result.addWhereClause(AnnotationSet.TABLE, AnnotationSet.FIELD_REVISION, 1);
		result.addCrossClause(AnnotationSet.TABLE, AnnotationSet.FIELD_USER_REFERENCE, AnnotationSet.TABLE_USER, AnnotationSet.FIELD_USER_ID);
		if (!oldModel)
			result.addCrossClause(AnnotationSet.TABLE, AnnotationSet.FIELD_TASK_REFERENCE, AnnotationSet.TABLE_TASK, AnnotationSet.FIELD_TASK_ID);
		
		if (options.hasTaskId() && !oldModel)
			result.addWhereClause(AnnotationSet.TABLE, AnnotationSet.FIELD_TASK_REFERENCE, options.getTaskId());
		if (options.hasTaskName() && !oldModel)
			result.addWhereClause(AnnotationSet.TABLE_TASK, AnnotationSet.FIELD_TASK_NAME, options.getTaskName());
		if (options.hasUserIds())
			result.addInClauseInteger(AnnotationSet.TABLE, AnnotationSet.FIELD_USER_REFERENCE, options.getUserIds());
		if (options.hasUserNames())
			result.addInClauseString(AnnotationSet.TABLE_USER, AnnotationSet.FIELD_USER_NAME, options.getUserNames());

		return result;
	}
	
	private static final Mapper<AlvisAEDocument,Integer> DOC_ID_MAPPER = new Mapper<AlvisAEDocument,Integer>() {
		@Override
		public Integer map(AlvisAEDocument x) {
			return x.getId();
		}
	};
	
	public void loadAnnotationSets(Logger logger, Connection connection, LoadOptions options, Collection<AlvisAEDocument> docs) throws SQLException, ParseException {
		Collection<Integer> documentIds;
		if (docs == null)
			documentIds = documents.keySet();
		else
			documentIds = Mappers.mappedCollection(DOC_ID_MAPPER, docs);
		SQLSelectQueryBuilder query = createAnnotationSetQuery(options, documentIds);
		ResultSet rs = query.runQuery(connection, logger);
		while (rs.next()) {
			AnnotationSet aset = new AnnotationSet(this, rs, true);
			aset.load(options, rs);
			if (options.isLoadDependencies()) {
				loadDependencies(logger, connection, options, aset);
				if (options.isAdjudicate()) {
					aset.mergeAdjudications(logger);
				}
			}
		}
	}

	public void loadAnnotationSets(Logger logger, Connection connection, LoadOptions options) throws SQLException, ParseException {
		loadAnnotationSets(logger, connection, options, null);
	}
	
	public void load(Logger logger, Connection connection, LoadOptions options) throws SQLException, ParseException {
		loadDocuments(logger, connection, options);
		loadAnnotationSets(logger, connection, options);
	}
}
