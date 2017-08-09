package com.facilio.beans;

import java.sql.Connection;

public interface RootBean {

	public Connection getConnection();
	
	public long getOrgId();
}