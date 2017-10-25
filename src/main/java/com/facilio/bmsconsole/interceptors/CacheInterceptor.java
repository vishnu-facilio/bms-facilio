package com.facilio.bmsconsole.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class CacheInterceptor extends AbstractInterceptor {

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		  HttpServletRequest request =
				  ServletActionContext.getRequest();
		  HttpServletResponse response = ServletActionContext.getResponse(); 
		 String id = request.getRequestURI() + 
			        request.getQueryString();
		 boolean cached_url ="GET".equals(request.getMethod())  && false;
		
		 boolean cache_available = false; // TODO write logic to find if the cache available
		 
		 if(cached_url)
		 {
			 String cachekey = null;
			 if(cache_available)
			 {
				 // render from cache
				 
			 }
			 else
			 {
				 // TODO
				 // wrap the response object

	           /*     GenericResponseWrapper responseWrapper =   new GenericResponseWrapper(response);	
	            *  ServletActionContext.setResponse(responseWrapper); 
	            */
				 String result = arg0.invoke();
					
				 //TODO 
				 // get the response from the wrapped response, store in redis cache with key as url+ orgid
				 return result;
			 }
		 }
		 else
		 {
			 String result = arg0.invoke();
			 return result;
		    
		 }
		return null;

		
	}

}
