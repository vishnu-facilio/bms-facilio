package com.facilio.fw;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanInvocationHandler implements InvocationHandler {
	private Object delegate;
	long orgid ;
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
        	result = method.invoke(delegate, args);
	    } catch (InvocationTargetException e) {
	        throw e;
	    } catch (Exception e) {
	        throw e;
	    }
        return result;
	}

}
