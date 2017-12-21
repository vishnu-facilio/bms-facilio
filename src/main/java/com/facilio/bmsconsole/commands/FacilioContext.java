package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.SQLException;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.apache.commons.chain.impl.ContextBase;

import com.facilio.transaction.FacilioConnectionPool;

public class FacilioContext extends ContextBase {
//	private Connection conn = null;
	
	public boolean isTransstarted() {
		return transstarted;
	}

	public void setTransstarted(boolean transstarted) {
		this.transstarted = transstarted;
	}

	boolean transstarted=false;
	/*public Connection getConnectionWithTransaction() throws SQLException, NotSupportedException, SystemException {
		if(conn == null)
		{
			//FacilioTransactionManager.INSTANCE.getTransactionManager().begin();
			if(transstarted)
			{
				throw new NotSupportedException();
			}
			conn = FacilioConnectionPool.getInstance().getConnection();
		}
		return conn;
	}
	
	*/
	
	public void commit() throws Exception {
		cleanup();
		if(transstarted) {
		//	FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
			transstarted = false;
		}
	}
	
	public void rollback() throws Exception {
		cleanup();
		if(transstarted) {
		//	FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
			transstarted = false;
		}
	}
	
	private void cleanup() throws Exception
	{
		//System.out.println("Closing connection ....");
		
	}
}


