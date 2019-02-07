package com.facilio.sql.mysql;

import com.facilio.sql.DBDeleteRecordBuilder;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeleteRecordBuilder extends DBDeleteRecordBuilder {

	public DeleteRecordBuilder(GenericDeleteRecordBuilder deleteRecordBuilder) {
		super(deleteRecordBuilder);
	}

	@Override
	public String constructDeleteStatement() {
		StringBuilder sql = new StringBuilder("DELETE ");
		sql.append(tablesToBeDeleted.toString())
			.append(" FROM ")
			.append(tableName)
			.append(joinBuilder.toString())
			.append(" WHERE ")
			.append(where.getWhereClause());
		
		return sql.toString();
	}
}
