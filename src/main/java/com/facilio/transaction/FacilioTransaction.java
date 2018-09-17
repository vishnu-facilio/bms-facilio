package com.facilio.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class FacilioTransaction implements Transaction {

	private int status=Status.STATUS_ACTIVE;
	private long transactionTimeout = 300_000L;
	private static Logger log = LogManager.getLogger(FacilioTransaction.class.getName());
	private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
	private String transactionId;

	public FacilioTransaction(String transactionId) {
		this.transactionId = transactionId;
	}

	private ArrayList<Connection> connections = new ArrayList<Connection>();

	public String getTransactionId() {
		return transactionId;
	}

	private void associateConnection(java.sql.Connection c) {
		connections.add(c);
	}

	public Connection getConnection() throws SQLException
	{
		for(int i=0;i< connections.size();i++)
		{
			FacilioConnection fc = (FacilioConnection)connections.get(i);
			if(fc.isFree())
			{
				fc.setFree(false);
				return fc;
			}
		}
		java.sql.Connection fc = new  FacilioConnection(FacilioConnectionPool.getInstance().getConnectionFromPool());
		fc.setAutoCommit(false);
		associateConnection(fc);
		return fc;
	}

	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
			SecurityException, SystemException {
		this.status = Status.STATUS_COMMITTING;
		for(int i=0;i< connections.size();i++) {
			FacilioConnection fc = (FacilioConnection)connections.get(i);
			try {
					fc.getPhysicalConnection().commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
			}
			try {
				fc.getPhysicalConnection().close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
			}
			
		}
		connections.clear();
		this.status =Status.STATUS_COMMITTED;

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
		return status;
	}

	@Override
	public void registerSynchronization(Synchronization arg0)
			throws RollbackException, IllegalStateException, SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback() throws IllegalStateException, SystemException {
		rollback(false);
	}

	public void rollback(boolean abortConnection) throws IllegalStateException, SystemException {
		this.status = Status.STATUS_ROLLING_BACK;

		for (Connection connection : connections) {
			FacilioConnection fc = (FacilioConnection) connection;
			try {
				fc.getPhysicalConnection().rollback();
			} catch (SQLException e) {
				log.info("Exception occurred ", e);
			}

			try {
				if (abortConnection) {
					fc.getPhysicalConnection().abort(EXECUTOR);
				} else {
					fc.getPhysicalConnection().close();
				}
			} catch (SQLException e) {
				log.info("Exception occurred ", e);
			}
		}
		connections.clear();
		this.status = Status.STATUS_ROLLEDBACK;
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		this.status =Status.STATUS_MARKED_ROLLBACK;
	}

	public void setTransactionTimeout(long transactionTimeout){
		this.transactionTimeout = transactionTimeout;
	}


	public long getTransactionTimeout(){
		return transactionTimeout;
	}
}
