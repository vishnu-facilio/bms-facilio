package com.facilio.fw;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.transaction.FacilioTransactionManager;

public class BeanInvocationHandler implements InvocationHandler {

	private static final Logger LOGGER = Logger.getLogger( BeanInvocationHandler.class.getName() );

	private boolean enableTransaction = false;
	private Object delegate;
	private long orgid = 0;
	
	private int transactiolevel = FacilioTransactionManager.TRANSACTION_NotSupported;

	public BeanInvocationHandler(Object obj, Long orgid) {
		this(obj, orgid, false);
	}

	public BeanInvocationHandler(Object obj, Long orgid, boolean enableTransaction) {
		this.delegate = obj;
		this.orgid = orgid;
		this.enableTransaction = enableTransaction;
	}

	public BeanInvocationHandler(Object obj, Long orgid, int  transactiolevel) {
		this.delegate = obj;
		this.orgid = orgid;
		if(transactiolevel==FacilioTransactionManager.TRANSACTION_NotSupported)
		{
		this.enableTransaction = false;
		}
		else 
		{
			this.enableTransaction = true;
			this.transactiolevel=transactiolevel;
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Object result;
		boolean isTransaction =false;
		try {
			Account oldAccount = AccountUtil.getCurrentAccount();

			if (orgid != 0) {
				System.out.println("the orgid is"+orgid);
				if (oldAccount == null || orgid != oldAccount.getOrg().getOrgId()) {
					AccountUtil.setCurrentAccount(orgid);
				}
			}


			if (enableTransaction) {
				TransactionManager tm = FacilioTransactionManager.INSTANCE.getTransactionManager();
				Transaction currentTransaction = tm.getTransaction();
				if (currentTransaction == null) {
					tm.begin();
					LOGGER.info("begin transaction for " + method.getName());
					isTransaction = true;
				} else {
					LOGGER.info("joining parent transaction for " + method.getName());
				}
			}
			result = method.invoke(delegate, args);

			if (enableTransaction && isTransaction) {
				LOGGER.info("commit transaction for " + method.getName());
				FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
			}
			if (orgid != 0 && oldAccount != null) {
				AccountUtil.setCurrentAccount(oldAccount);
			}

		}  catch (Exception e) {
			LOGGER.log(Level.INFO,"exception for  for " + method.getName(),e);
			if (enableTransaction && isTransaction) {
				FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
				LOGGER.info("rollback transaction for " + method.getName());
			}
			throw e;
		}
		return result;
	}

}
