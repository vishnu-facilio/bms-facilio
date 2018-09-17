package com.facilio.transaction;

import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.facilio.aws.util.AwsUtil;

public class FTransactionManager implements TransactionManager {
	
	private static final FTransactionManager INSTANCE = new FTransactionManager();

	public static TransactionManager getTransactionManager() {
		return INSTANCE;
	}
	
	private FTransactionManager() {
    }

	private static ThreadLocal<FacilioTransaction> currentTransaction = new ThreadLocal<FacilioTransaction>();

	public static ConcurrentHashMap<FacilioTransaction, Long> getTransactionTimeoutMap() {
		return TRANSACTION_TIMEOUT_MAP;
	}

	private static final ConcurrentHashMap<FacilioTransaction, Long> TRANSACTION_TIMEOUT_MAP = new ConcurrentHashMap<>();

	private String getTransactionId() {
		return  Thread.currentThread().getName();
	}

	@Override
	public void begin() throws NotSupportedException, SystemException {
        FacilioTransaction currenttrans = currentTransaction.get();
		if("true".equals(AwsUtil.getConfig("enable.transaction")) && currenttrans == null) {
			currenttrans =  new FacilioTransaction(getTransactionId());
			currentTransaction.set(currenttrans);
			TRANSACTION_TIMEOUT_MAP.put(currenttrans, System.currentTimeMillis());
		}
	}

	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
			SecurityException, IllegalStateException, SystemException {

		FacilioTransaction currenttrans = currentTransaction.get();
		if(currenttrans != null) {
			currenttrans.commit();
			currentTransaction.remove();
			TRANSACTION_TIMEOUT_MAP.remove(currenttrans);
		}

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
		FacilioTransaction currenttrans = currentTransaction.get();
		if(currenttrans != null) {
			currenttrans.rollback();
			currentTransaction.remove();
			TRANSACTION_TIMEOUT_MAP.remove(currenttrans);
		}
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		currentTransaction.get().setRollbackOnly();
	}

	@Override
	public void setTransactionTimeout(int arg0) throws SystemException {
		FacilioTransaction currenttrans = currentTransaction.get();
		if(currenttrans != null) {
			currenttrans.setTransactionTimeout(arg0);
		}
	}

	@Override
	public Transaction suspend() throws SystemException {
		return null;
	}

}
