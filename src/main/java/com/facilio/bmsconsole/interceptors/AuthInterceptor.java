package com.facilio.bmsconsole.interceptors;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.facilio.fw.OrgInfo;
import com.facilio.fw.UserInfo;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthInterceptor extends AbstractInterceptor {

	private static String HOSTNAME = null;
	@Override
	public void init() {
		
		super.init();
	}
	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		
	      String output = "Pre-Processing"; 
	      System.out.println(output);

			String username = (String)ActionContext.getContext().getSession().get("USERNAME");
					
					HttpServletRequest request = ServletActionContext.getRequest();
					String subdomain = request.getRemoteHost();
					
					if(HOSTNAME==null)
					{
						HOSTNAME = (String)ActionContext.getContext().getApplication().get("DOMAINNAME");
					}
					subdomain = subdomain.replaceAll(HOSTNAME, "");
					
					//ActionContext.getContext().getParameters()

        	
        
	      if(username==null)
	      {
	    	  return Action.LOGIN;
	      }
	  	try {
			OrgInfo.validateOrgInfo(subdomain, username);
		} catch (Exception e) {
			
			 return "unauthorized";
		}
	      /* let us call action or next interceptor */
	      String result = arg0.invoke();

	      /* let us do some post-processing */
	      output = "Post-Processing"; 
	      System.out.println(output);

	      return result;
	}

}
