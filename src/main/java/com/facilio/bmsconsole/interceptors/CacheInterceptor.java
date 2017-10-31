package com.facilio.bmsconsole.interceptors;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class CacheInterceptor extends AbstractInterceptor {

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		  HttpServletRequest request =
				  ServletActionContext.getRequest();
		  HttpServletResponse response = ServletActionContext.getResponse(); 
		 String id = request.getRequestURI() + ( request.getQueryString()!=null ? "?"+request.getQueryString():"");
		 
		 Parameter cache = ActionContext.getContext().getParameters().get("cache");
		 boolean cached_url ="GET".equals(request.getMethod())  && (cache !=null && cache.getValue()!=null && cache.getValue().equals("org"));
		
		//System.out.println("cache interceptor "+request.getMethod() +"-"+cache.getValue() +" chache object "+cache);
		 
		 if(cached_url)
		 {
			 String cachekey = null;
			 System.out.println("validating cache..for "+id);
			 boolean cache_available = false; // TODO write logic to find if the cache available
			 if(cache_available)
			 {
				 // render from cache
				 
			 }
			 else
			 {
				 // TODO
				
				 
			/*	 ByteArrayOutputStream baos = new ByteArrayOutputStream();
			        CacheResponseWrapper wrappedResponse =
			          new CacheResponseWrapper(response, baos);
			        ServletActionContext.setResponse(wrappedResponse); */
			        
			        request.setAttribute("cacheurl",id);
				 String result = arg0.invoke();
				/* String s =baos.toString();		 
				 System.out.println("The response to be stored "+ s);*/
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
