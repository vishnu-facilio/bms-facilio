package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.db.ResponseCacheUtil;
import com.facilio.filters.MultiReadServletRequest;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

public class CacheInterceptor extends AbstractInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		
		MultiReadServletRequest multiReadServletRequest = new MultiReadServletRequest(request);
		ServletActionContext.setRequest(multiReadServletRequest);
		
		if (multiReadServletRequest.isCachedRequest()) {
//			HttpServletRequestWrapper requestWrapper = ((HttpServletRequestWrapper) request);
//			if (requestWrapper.getRequest() instanceof MultiReadServletRequest && ((MultiReadServletRequest) requestWrapper.getRequest()).isCachedRequest()) {
//				MultiReadServletRequest multiReadServletRequest = ((MultiReadServletRequest) requestWrapper.getRequest());
				
				String requestURI = multiReadServletRequest.getRequestURI();
		        String contentHash = multiReadServletRequest.getContentHash();
				long userId = AccountUtil.getCurrentUser().getId();
				long orgId = AccountUtil.getCurrentOrg().getId();

				Object object = ResponseCacheUtil.getCache(orgId, userId, requestURI, contentHash);
				if (object != null) {
					return "cachedResponse";
				}
//			}
		}
		
		return arg0.invoke();
//		  HttpServletRequest request =
//				  ServletActionContext.getRequest();
//		//  HttpServletResponse response = ServletActionContext.getResponse(); 
//		 String id = request.getRequestURI() + ( request.getQueryString()!=null ? "?"+request.getQueryString():"");
//		 
//		 Parameter cache = ActionContext.getContext().getParameters().get("cache");
//		 boolean cached_url ="GET".equals(request.getMethod())  && (cache !=null && cache.getValue()!=null && cache.getValue().equals("org"));
//		
//		//System.out.println("cache interceptor "+request.getMethod() +"-"+cache.getValue() +" chache object "+cache);
//		 String result = null;		 
//		 Object expireat = null;//ActionContext.getContext().getValueStack().findValue("cacheControl");
//		 Parameter cachetype = ActionContext.getContext().getParameters().get("cachetype");
//		 if(cachetype !=null && cachetype.getValue()!=null )
//		 {
//			expireat = CacheControl.getCacheControl(Integer.parseInt(cachetype.getValue()));
//		 }
//		 if(expireat !=null && expireat instanceof CacheControl)
//		 {
//			 CacheControl c = (CacheControl)expireat;
//			 System.out.println("response header " + c );
//			 
//			 final ActionContext ac = arg0.getInvocationContext();
//			    HttpServletResponse response = (HttpServletResponse) ac.get(StrutsStatics.HTTP_RESPONSE);
//			    
//
//			    response.setHeader("Cache-control", c.getCachecontrol());
//			    response.setHeader("Expires", c.getExpires());
//		 }
//
//		 if(cached_url)
//		 {
//			 System.out.println("validating cache..for "+id);
//			 boolean cache_available = false; // TODO write logic to find if the cache available
//			 if(cache_available)
//			 {
//				 // render from cache
//				 
//			 }
//			 else
//			 {
//				 // TODO
//				
//				 
//		
//			        
//			        request.setAttribute("cacheurl",id);
//				  result = arg0.invoke();
//				
//				 
//			
//				 
//				// return result;
//			 }
//		 }
//		 else
//		 {
//			
//			  result = arg0.invoke();
//			
//		    
//		 }
//			return result;

		
	}

}
