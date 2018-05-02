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
	
	private static final FTransactionManager INSTANCE = new FTransactionManager();

	public static TransactionManager getTransactionManager() {
		return INSTANCE;
	}
	
	private FTransactionManager() {
    }

	private static ThreadLocal<FacilioTransaction> currentTransaction = new ThreadLocal<FacilioTransaction>();

	@Override
	public void begin() throws NotSupportedException, SystemException {
        FacilioTransaction currenttrans = currentTransaction.get();
		if(currenttrans == null) {
			currenttrans =  new FacilioTransaction();
			currentTransaction.set(currenttrans);
		}
	}

	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
			SecurityException, IllegalStateException, SystemException {
		
		currentTransaction.get().commit();
		currentTransaction.remove();

	}

	@Override
	public int getStatus() throws SystemException {
		return currentTransaction.get().getStatus();
	}

	@Override
	public Transaction getTransaction() throws SystemException {
		FacilioTransaction currenttrans = currentTransaction.get();
		return currenttrans;
	}

	@Override
	public void resume(Transaction arg0) throws InvalidTransactionException, IllegalStateException, SystemException {

	}

	@Override
	public void rollback() throws IllegalStateException, SecurityException, SystemException {
		getTransaction().rollback();
		currentTransaction.remove();
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		currentTransaction.get().setRollbackOnly();
	}

	@Override
	public void setTransactionTimeout(int arg0) throws SystemException {
		//currentTransaction.get().
	}

	@Override
	public Transaction suspend() throws SystemException {
		return null;
	}

}
