package com.facilio.transaction;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public enum FacilioTransactionManager {
	INSTANCE;
	private static Logger log = LogManager.getLogger(FacilioTransactionManager.class.getName());
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
		
		    return FTransactionManager.getTransactionManager();
		
		
	}
	public static int TRANSACTION_NotSupported=-1;
	public static int TRANSACTION_REQUIRED=0;
	public static int TRANSACTION_REQUIRES_NEW=1;
	public static int TRANSACTION_Mandatory=2;


	
}
