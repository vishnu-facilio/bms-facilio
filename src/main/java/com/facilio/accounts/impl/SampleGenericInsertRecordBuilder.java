package com.facilio.accounts.impl;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;

public class SampleGenericInsertRecordBuilder extends GenericInsertRecordBuilder{

private String tableName;
	
	@Override
	public GenericInsertRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return super.table(tableName);
	}
	
	@Override
	public void handleInsertOrgFields() {
		if (tableName.equalsIgnoreCase("Account_Users") || tableName.equalsIgnoreCase("Account_ORG_Users")) {
			return;
		}
		super.handleInsertOrgFields();
	}
}
