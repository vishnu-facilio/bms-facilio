package com.facilio.fw;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;

public class BeanInvocationHandler implements InvocationHandler {

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
		
		try {
			Account oldAccount = AccountUtil.getCurrentAccount();

			if (orgid != 0) {
				if (oldAccount == null || orgid != oldAccount.getOrg().getOrgId()) {
					AccountUtil.setCurrentAccount(orgid);
				}
			}

			// TODO switch context to orgid
		
			result = method.invoke(delegate, args);
			
			if (orgid != 0 && oldAccount != null) {
				AccountUtil.setCurrentAccount(oldAccount);
			}
		
		} catch (InvocationTargetException e) {
			
			throw e;
		} catch (Exception e) {
			
			throw e;
		}
		finally {
			
		}
		return result;
	}

}
