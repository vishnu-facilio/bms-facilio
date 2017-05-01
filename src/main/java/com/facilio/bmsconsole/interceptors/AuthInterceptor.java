package com.facilio.bmsconsole.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthInterceptor extends AbstractInterceptor {

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		// TODO Auto-generated method stub
		/* let us do some pre-processing */
	      String output = "Pre-Processing"; 
	      System.out.println(output);

	      /* let us call action or next interceptor */
	      String result = arg0.invoke();

	      /* let us do some post-processing */
	      output = "Post-Processing"; 
	      System.out.println(output);

	      return result;
	}

}
