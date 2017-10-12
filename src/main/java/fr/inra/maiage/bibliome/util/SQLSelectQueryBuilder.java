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

package fr.inra.maiage.bibliome.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

public class SQLSelectQueryBuilder {
	private final String schema;
	private boolean distinct = false;
	private final StringBuilder select = new StringBuilder();
	private final StringBuilder from = new StringBuilder();
	private final StringBuilder where = new StringBuilder();

	public SQLSelectQueryBuilder(String schema, CharSequence select, CharSequence from, CharSequence where) {
		super();
		this.schema = schema;
		this.select.append(select);
		this.from.append(from);
		this.where.append(where);
	}
	
	public SQLSelectQueryBuilder(String schema) {
		this(schema, "", "", "");
	}
	
	public SQLSelectQueryBuilder(SQLSelectQueryBuilder source) {
		this(source.schema, source.select, source.from, source.where);
	}
	
	private static void addIdentifier(StringBuilder sb, String prefix, String id) {
		if (prefix != null) {
			sb.append(prefix);
			sb.append('.');
		}
		sb.append(id);		
	}
	
	private static boolean addIdentifiers(StringBuilder sb, boolean notFirst, String prefix, Collection<String> identifiers) {
		if (identifiers == null)
			return notFirst;
		if (identifiers.isEmpty())
			return notFirst;
		for (String id : identifiers) {
			if (notFirst)
				sb.append(", ");
			else
				notFirst = true;
			addIdentifier(sb, prefix, id);
		}
		return true;
	}

	private static boolean addIdentifiers(StringBuilder sb, boolean notFirst, String prefix, String... identifiers) {
		return addIdentifiers(sb, notFirst, prefix, Arrays.asList(identifiers));
	}
	
	private boolean hasSelect() {
		return select.length() > 0;
	}
	
	private boolean hasFrom() {
		return from.length() > 0;
	}
	
	private boolean hasWhere() {
		return where.length() > 0;
	}
	
	public void addSelect(String... fields) {
		addIdentifiers(select, hasSelect(), null, fields);
	}
	
	public void addFrom(String... tables) {
		addIdentifiers(from, hasFrom(), schema, tables);
	}

	
	private static interface SQLValue<T> {
		void toSQL(StringBuilder query, T value);
	}
	
	private static final SQLValue<Integer> SQL_INTEGER = new SQLValue<Integer>() {
		@Override
		public void toSQL(StringBuilder query, Integer value) {
			query.append(value);
		}
	};
	
	private static final SQLValue<String> SQL_STRING = new SQLValue<String>() {
		@Override
		public void toSQL(StringBuilder query, String value) {
			query.append('\'');
			for (int i = 0; i < value.length(); ++i) {
				char c = value.charAt(i);
				if (c == '\'')
					query.append("\\'");
				else
					query.append(c);
			}
			query.append('\'');
		}
	};

	private void addWhereClausePreamble() {
		if (hasWhere()) {
			where.append(" AND ");
		}
	}
	
	public void addWhereClause(String clause) {
		addWhereClausePreamble();
		where.append(clause);
	}
	
	private <T> void addWhereClause(SQLValue<T> sqlValue, String table, String field, T value) {
		addWhereClausePreamble();
		addIdentifier(where, table, field);
		where.append(" = ");
		sqlValue.toSQL(where, value);
	}
	
	public void addWhereClause(String table, String field, Integer value) {
		addWhereClause(SQL_INTEGER, table, field, value);
	}
	
	public void addWhereClause(String table, String field, String value) {
		addWhereClause(SQL_STRING, table, field, value);
	}
	
	public void addCrossClause(String leftTable, String leftField, String rightTable, String rightField) {
		addWhereClausePreamble();
		addIdentifier(where, leftTable, leftField);
		where.append(" = ");
		addIdentifier(where, rightTable, rightField);
	}
	
	private <T> void addInClause(SQLValue<T> sqlValue, String table, String field, Collection<T> values) {
		addWhereClausePreamble();
		addIdentifier(where, table, field);
		where.append(" IN (");
		boolean notFirst = false;
		for (T v : values) {
			if (notFirst)
				where.append(", ");
			else
				notFirst = true;
			sqlValue.toSQL(where, v);
		}
		where.append(')');
	}
	
	public void addInClauseInteger(String table, String field, Collection<Integer> values) {
		addInClause(SQL_INTEGER, table, field, values);
	}
	
	public void addInClauseString(String table, String field, Collection<String> values) {
		addInClause(SQL_STRING, table, field, values);
	}
	
	public String getQuery(Logger logger) {
		StringBuilder sb = new StringBuilder("SELECT ");
		if (distinct) {
			sb.append("DISTINCT ");
		}
		sb.append(select);
		if (hasFrom()) {
			sb.append(" FROM ");
			sb.append(from);
		}
		if (hasWhere()) {
			sb.append(" WHERE ");
			sb.append(where);
		}
		String result = sb.toString();
		if (logger != null) {
			logger.finer(result);
		}
		return result;
	}
	
	public ResultSet runQuery(Connection connection, Logger logger) throws SQLException {
		Statement statement = connection.createStatement();
		String query = getQuery(logger);
		return statement.executeQuery(query);
	}
	
	public void clear() {
		select.setLength(0);
		from.setLength(0);
		where.setLength(0);
		distinct = false;
	}
	
	public String getSchema() {
		return schema;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
}
