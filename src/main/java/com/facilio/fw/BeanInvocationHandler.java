package com.facilio.fw;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanInvocationHandler implements InvocationHandler {
	private Object delegate;
	long orgid =0;;
	public BeanInvocationHandler(Object ob,Long orgid)

	{
		this.delegate=ob;
		this.orgid=orgid;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result;
        try{
        	// TODO switch context to orgid
        	ThreadLocal<OrgInfo> thlocal = new ThreadLocal<OrgInfo>();
        	OrgInfo oldorginfo = OrgInfo.getCurrentOrgInfo();
        	boolean switchback = false;
        	if(orgid!=0)
        	{
        		OrgInfo.setCurrentOrgInfo(new OrgInfo(orgid));
        		switchback = true;
        	}
        	result = method.invoke(delegate, args);
        	if(switchback)
        	{
        	OrgInfo.setCurrentOrgInfo(oldorginfo);
        	}
        	
        } catch (InvocationTargetException e) {
	        throw e;
	    } catch (Exception e) {
	        throw e;
	    }
        return result;
	}

}
