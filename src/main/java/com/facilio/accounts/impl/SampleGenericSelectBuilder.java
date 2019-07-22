package com.facilio.accounts.impl;

import com.facilio.db.builder.GenericSelectRecordBuilder;

public class SampleGenericSelectBuilder extends GenericSelectRecordBuilder {

	private String tableName;
	
	@Override
	public GenericSelectRecordBuilder table(String tableName) {
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
