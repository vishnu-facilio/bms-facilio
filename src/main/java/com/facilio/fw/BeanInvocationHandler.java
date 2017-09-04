package com.facilio.fw;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.facilio.bmsconsole.util.OrgApi;

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
			
			OrgInfo oldorginfo = null;


			if(orgid!=0)
			{
			 OrgInfo orginfo = 	OrgApi.getOrgInfo(orgid);
			 oldorginfo = OrgInfo.getCurrentOrgInfo();
			 OrgInfo.setCurrentOrgInfo(orginfo);
			}
			//Connection oldConn = BeanFactory.setConnection(this.conn);

			// TODO switch context to orgid
		
			result = method.invoke(delegate, args);
			
			if(orgid!=0 && oldorginfo!=null)
			{
			// OrgInfo orginfo = 	OrgApi.getOrgInfo(orgid);
			 // oldorginfo = OrgInfo.getCurrentOrgInfo();
			 OrgInfo.setCurrentOrgInfo(oldorginfo);
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
