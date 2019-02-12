package com.facilio.sql.mssql;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.sql.DBSelectRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class SelectRecordBuilder extends DBSelectRecordBuilder {
	private static final Logger LOGGER = LogManager.getLogger(SelectRecordBuilder.class.getName());

	public SelectRecordBuilder(GenericSelectRecordBuilder selectBuilder) { //Do not call after calling getProps
		super(selectBuilder);
	}

	public String constructSelectStatement() {
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		
		boolean isFirst = true;
		for(FacilioField field : selectFields) {
			if(isFirst) {
				isFirst = false;
			}
			else {
				sql.append(", ");
			}
			sql.append(field.getCompleteColumnName())
				.append(" AS ")
				.append("\"" + field.getName() + "\"")
				.append("");
		}
		
		sql.append(" FROM ")
			.append("\"" + tableName + "\"");
		
		sql.append(joinBuilder.toString());
		
		if(where.getWhereClause() != null && !where.getWhereClause().isEmpty()) {
			String whereString = where.getWhereClause();
			whereString = whereString.replaceAll("true", "1");
			whereString = whereString.replaceAll("false", "0");
			sql.append(" WHERE ")
				.append(whereString);
		}
		
		if(groupBy != null && !groupBy.isEmpty()) {
			sql.append(" GROUP BY ")
				.append(groupBy);
		}
		
		if(having != null && !having.isEmpty()) {
			sql.append(" HAVING ")
				.append(having);
		}
		
		if(orderBy != null && !orderBy.isEmpty()) {
			sql.append(" ORDER BY ")
				.append(orderBy);
		}
		
		if(orderBy != null && limit != -1) {
			if(offset < 0) {
				offset = 0;
			}
			sql.append(" OFFSET ")
			.append(offset + " ROWS ");
			
			sql.append(" FETCH NEXT ")
				.append(limit + " ROWS ONLY ");
		}
		
		String sqlString = sql.toString();
//		sqlString = handleKeywords(sqlString);
		return sqlString;
	}

	private String handleKeywords(String query) {
		for (String keyword : keywords) {
			query = query.replace(keyword, "\"" + keyword + "\"");
		}
		return query;
	}

	private String handleReseveredKeywords(String sqlString) {
//		for (String reservedKeyword : reservedKeywords) {
//			sqlString = sqlString.replaceAll(reservedKeyword, "\"" + reservedKeyword + "\"");
//		}
		return sqlString;
	}

	private static String[] keywords = {"FacilioFile"};
}
