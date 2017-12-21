package com.facilio.fw;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.transaction.FacilioTransactionManager;

public class BeanInvocationHandler implements InvocationHandler {

	private static final Logger LOGGER = Logger.getLogger( BeanInvocationHandler.class.getName() );

	private Object delegate;
	long orgid = 0;
//	private Connection conn;

	public BeanInvocationHandler(Object obj, Long orgid) {
		this.delegate = obj;
		this.orgid = orgid;
	}

	/*public BeanInvocationHandler(Object obj, Long orgid, Connection conn) {
		this.delegate = obj;
		this.orgid = orgid;
		this.conn = conn;
	}*/

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Object result;
		boolean istransaction =false;
		try {
			Account oldAccount = AccountUtil.getCurrentAccount();

			if (orgid != 0) {
				if (oldAccount == null || orgid != oldAccount.getOrg().getOrgId()) {
					AccountUtil.setCurrentAccount(orgid);
				}
			}

			// TODO switch context to orgid
		LOGGER.info("calling method "+method.getName());
		TransactionManager tm = FacilioTransactionManager.INSTANCE.getTransactionManager();
		Transaction currenttrans = tm.getTransaction();
		if (currenttrans == null) {
			tm.begin();
			LOGGER.info("begin transaction for "+method.getName() );
			
			istransaction = true;
		}
		else
		{
			LOGGER.info("joining parent transaction for "+method.getName() );
		}
			result = method.invoke(delegate, args);
			LOGGER.info("finish method"+method.getName());

			if(istransaction)
			{
				LOGGER.info("commit transaction for "+method.getName());
				FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
			}
			if (orgid != 0 && oldAccount != null) {
				AccountUtil.setCurrentAccount(oldAccount);
			}
		
		}  catch (Exception e) {
			if(istransaction)
			{
				FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
				LOGGER.info("rollback transaction for "+method.getName());
			}
			throw e;
		}
		finally {
			
		}
		return result;
	}

}
