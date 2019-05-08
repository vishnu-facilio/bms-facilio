package com.facilio.db.builder.mysql;

import com.facilio.db.builder.DBDeleteRecordBuilder;
import com.facilio.db.builder.GenericDeleteRecordBuilder;

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
