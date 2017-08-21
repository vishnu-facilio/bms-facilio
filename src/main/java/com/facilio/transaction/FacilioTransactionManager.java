package com.facilio.transaction;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;


public enum FacilioTransactionManager {
	INSTANCE;
	
//	private TransactionManager txn = null;
	private FacilioTransactionManager() {
	/*	try {
			txn = (TransactionManager) InitialContext.doLookup("java:comp/env/TransactionManager");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ExceptionInInitializerError("Unable to initialize Transaction manager due to the following error : \n"+e.getMessage());
		}
		*/
	}
	
	public TransactionManager getTransactionManager() {
		try {
			TransactionManager	txn = (TransactionManager) InitialContext.doLookup("java:comp/env/TransactionManager");
			return txn;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ExceptionInInitializerError("Unable to initialize Transaction manager due to the following error : \n"+e.getMessage());
		}
	}
}
