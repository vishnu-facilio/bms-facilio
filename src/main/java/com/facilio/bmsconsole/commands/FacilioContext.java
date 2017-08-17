package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.chain.impl.ContextBase;

import com.facilio.transaction.FacilioConnectionPool;

public class FacilioContext extends ContextBase {
	private Connection transactedConn = null, readOnlyConn = null;
	private boolean isTransactedConnOpen = false, isReadOnlyConnOpen = false;
	
	
	public Connection getConnectionWithTransaction() throws SQLException {
		if(transactedConn == null || !isTransactedConnOpen)
		{
			transactedConn = FacilioConnectionPool.getInstance().getConnection();
			isTransactedConnOpen = true;
			transactedConn.setAutoCommit(false);
		}
		return transactedConn;
	}
	
	public Connection getConnectionWithoutTransaction() throws SQLException {
		if(readOnlyConn == null || !isReadOnlyConnOpen) {
			readOnlyConn = FacilioConnectionPool.getInstance().getConnection();
			isReadOnlyConnOpen = true;
		}
		return readOnlyConn;
	}
	
	public void commit() throws Exception {
		if(transactedConn != null) {
			transactedConn.commit();
		}
		cleanup();
	}
	
	public void rollback() throws Exception {
		if(transactedConn != null) {
			transactedConn.rollback();
		}
		cleanup();
	}
	
	private void cleanup() throws Exception
	{
		System.out.println("Closing connection ....");
		if(transactedConn != null) {
			transactedConn.close();
			transactedConn = null;
			isTransactedConnOpen = false;
		}
		
		if(readOnlyConn != null) {
			readOnlyConn.close();
			readOnlyConn = null;
			isReadOnlyConnOpen = false;
		}
	}
}


