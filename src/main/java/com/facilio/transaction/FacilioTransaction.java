package com.facilio.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

public class FacilioTransaction implements Transaction {
	
	ArrayList<Connection> connections = new ArrayList<Connection>();
	
	public void associateConnection(java.sql.Connection c)
	{
		connections.add(c);
	}
	public void disAssociateConnection(java.sql.Connection c)
	{
		connections.remove(c);
	}
	
	public Connection getConnection() throws SQLException
	{
		for(int i=0;i< connections.size();i++)
		{
			FacilioConnection fc = (FacilioConnection)connections.get(i);
			if(fc.isFree())
			{
				return fc;
			}
			
		}
		java.sql.Connection fc = FacilioConnectionPool.getInstance().getConnectionFromPool();
		fc.setAutoCommit(false);
		associateConnection(fc);
		return fc;
	}
	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
			SecurityException, SystemException {
		for(int i=0;i< connections.size();i++)
		{
			FacilioConnection fc = (FacilioConnection)connections.get(i);
			try {
				
				
					fc.getPhysicalConnection().commit();
					fc.getPhysicalConnection().close();
					disAssociateConnection(fc);
				

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public boolean delistResource(XAResource arg0, int arg1) throws IllegalStateException, SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean enlistResource(XAResource arg0) throws RollbackException, IllegalStateException, SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getStatus() throws SystemException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void registerSynchronization(Synchronization arg0)
			throws RollbackException, IllegalStateException, SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback() throws IllegalStateException, SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		// TODO Auto-generated method stub

	}

}
