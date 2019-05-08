package com.facilio.db.builder.mssql;

import com.facilio.db.builder.DBDeleteRecordBuilder;
import com.facilio.db.builder.GenericDeleteRecordBuilder;

public class DeleteRecordBuilder extends DBDeleteRecordBuilder {

	public DeleteRecordBuilder(GenericDeleteRecordBuilder deleteRecordBuilder) {
		super(deleteRecordBuilder);
	}

	@Override
	public String constructDeleteStatement() {
		StringBuilder sql = new StringBuilder("DELETE ");
		String whereString = where.getWhereClause();
		whereString = whereString.replaceAll("true", "1");
		whereString = whereString.replaceAll("false", "0");
		sql.append(tablesToBeDeleted)
			.append(" FROM ")
			.append(tableName)
			.append(joinBuilder)
			.append(" WHERE ")
			.append(whereString);
		
		return sql.toString();
	}
}
