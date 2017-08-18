package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.SQLException;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.apache.commons.chain.impl.ContextBase;

import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.transaction.FacilioTransactionManager;

public class FacilioContext extends ContextBase {
	private Connection conn = null;
	
	
	public Connection getConnectionWithTransaction() throws SQLException, NotSupportedException, SystemException {
		if(conn == null)
		{
			FacilioTransactionManager.INSTANCE.getTransactionManager().begin();
			conn = FacilioConnectionPool.getInstance().getConnection();
		}
		return conn;
	}
	
	public Connection getConnectionWithoutTransaction() throws SQLException, NotSupportedException, SystemException {
		return getConnectionWithTransaction();
	}
	
	public void commit() throws Exception {
		cleanup();
		if(conn != null) {
			FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
		}
	}
	
	public void rollback() throws Exception {
		cleanup();
		if(conn != null) {
			FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
		}
	}
	
	private void cleanup() throws Exception
	{
		System.out.println("Closing connection ....");
		if(conn != null) {
			conn.close();
			conn = null;
		}
	}
}


