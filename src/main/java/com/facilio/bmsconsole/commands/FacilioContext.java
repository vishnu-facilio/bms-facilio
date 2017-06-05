package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.chain.impl.ContextBase;

import com.facilio.transaction.FacilioConnectionPool;

public class FacilioContext extends ContextBase {
	java.sql.Connection conn = null;
	boolean isConnectionOpen = false;
	public Connection getConnection() throws SQLException
	{
		if(conn==null || !isConnectionOpen)
		{
			conn = FacilioConnectionPool.getInstance().getConnection();
			
		}
		return conn;
	}
	
	public void cleanup() throws Exception
	{
		conn.close();
		conn =null;
		isConnectionOpen = false;
	}

}
