package com.facilio.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

public class FTransactionManager implements TransactionManager {
	
	private static FTransactionManager instance = null;
	public static TransactionManager getTransactionManager()
	{
		if(instance==null)
		{
			instance = new FTransactionManager();
		}
		return instance;
		
	}
	
	private FTransactionManager()
	{
		
	}
	private static ThreadLocal<FacilioTransaction>  currenttransaction = new ThreadLocal<FacilioTransaction>();

	@Override
	public void begin() throws NotSupportedException, SystemException {
    FacilioTransaction currenttrans = currenttransaction.get();
		if(currenttrans ==null)
		{
			currenttrans =  new FacilioTransaction();
			currenttransaction.set(currenttrans);
		}
		//return currenttrans;
	}

	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
			SecurityException, IllegalStateException, SystemException {
		
		//con.commit
		// TODO Auto-generated method stub

	}

	@Override
	public int getStatus() throws SystemException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Transaction getTransaction() throws SystemException {
		// TODO Auto-generated method stub
		FacilioTransaction currenttrans = currenttransaction.get();
		
		return currenttrans;
	}

	@Override
	public void resume(Transaction arg0) throws InvalidTransactionException, IllegalStateException, SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback() throws IllegalStateException, SecurityException, SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTransactionTimeout(int arg0) throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public Transaction suspend() throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

}
