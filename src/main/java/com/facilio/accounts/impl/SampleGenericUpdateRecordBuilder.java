package com.facilio.accounts.impl;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;

public class SampleGenericUpdateRecordBuilder extends GenericUpdateRecordBuilder{

private String tableName;
	
	@Override
	public GenericUpdateRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return super.table(tableName);
	}
	
	@Override
	public void handleOrgId() {
		if (tableName.equalsIgnoreCase("Account_Users") || tableName.equalsIgnoreCase("Account_ORG_Users")) {
			return;
		}
		super.handleOrgId();
	}
}
