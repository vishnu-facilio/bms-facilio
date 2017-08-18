package com.facilio.fw;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

import com.facilio.transaction.FacilioConnectionPool;

public class BeanInvocationHandler implements InvocationHandler {

	private Object delegate;
	long orgid = 0;
	private Connection conn;

	public BeanInvocationHandler(Object obj, Long orgid) {
		this.delegate = obj;
		this.orgid = orgid;
	}

	public BeanInvocationHandler(Object obj, Long orgid, Connection conn) {
		this.delegate = obj;
		this.orgid = orgid;
		this.conn = conn;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Object result;
		boolean localConn = false;
		try {

			if (this.conn == null) {
				System.out.println("Connection NULL #########  !!!!!!!!!!");
				this.conn = FacilioConnectionPool.getInstance().getConnection();
				this.conn.setAutoCommit(false);
				localConn = true;
			}
			//Connection oldConn = BeanFactory.setConnection(this.conn);

			// TODO switch context to orgid
		
			result = method.invoke(delegate, args);
			
		//	BeanFactory.setConnection(oldConn);
			if (localConn) {
				this.conn.commit();
			}
		} catch (InvocationTargetException e) {
			if (localConn && this.conn != null) {
				this.conn.rollback();
			}
			throw e;
		} catch (Exception e) {
			if (localConn && this.conn != null) {
				this.conn.rollback();
			}
			throw e;
		}
		finally {
			if (localConn && this.conn != null) {
				this.conn.close();
			}
		}
		return result;
	}

}
