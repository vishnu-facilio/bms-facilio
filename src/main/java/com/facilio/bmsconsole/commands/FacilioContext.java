package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.chain.impl.ContextBase;

import com.facilio.transaction.FacilioConnectionPool;

public class FacilioContext extends ContextBase {
	java.sql.Connection conn = null;
	boolean isConnectionOpen = false;
	boolean isAutoCommit = false;
	
	
	public Connection getConnection() throws SQLException
	{
		if(conn==null || !isConnectionOpen)
		{
			conn = FacilioConnectionPool.getInstance().getConnection();
			isAutoCommit = false;
			conn.setAutoCommit(false);
			
		}
		return conn;
	}
	
	public void cleanup() throws Exception
	{
		if(!isAutoCommit)
		{
		conn.commit();
		}
		conn.close();
		conn =null;
		isConnectionOpen = false;
	}

}
