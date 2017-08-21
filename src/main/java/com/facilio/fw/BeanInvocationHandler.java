package com.facilio.fw;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

import com.facilio.transaction.FacilioConnectionPool;

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
		boolean localConn = false;
		try {

			
			//Connection oldConn = BeanFactory.setConnection(this.conn);

			// TODO switch context to orgid
		
			result = method.invoke(delegate, args);
			
		
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
