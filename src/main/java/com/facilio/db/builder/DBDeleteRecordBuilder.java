package com.facilio.db.builder;

import org.apache.commons.lang3.StringUtils;

public abstract class DBDeleteRecordBuilder {
	
	protected String tableName;
	protected WhereBuilder where = new WhereBuilder();
	protected String joinBuilder;
	protected String tablesToBeDeleted;
	
	protected DBDeleteRecordBuilder(GenericDeleteRecordBuilder deleteRecordBuilder) {
		this.tableName = deleteRecordBuilder.getTableName();
		this.joinBuilder = deleteRecordBuilder.getJoinBuilder().toString();
		this.tablesToBeDeleted = StringUtils.join(deleteRecordBuilder.getTablesToBeDeleted(), ",");
		
		if (deleteRecordBuilder.getWhere() != null) {
			this.where = new WhereBuilder(deleteRecordBuilder.getWhere());
		}
	}

	public abstract String constructDeleteStatement();
}
