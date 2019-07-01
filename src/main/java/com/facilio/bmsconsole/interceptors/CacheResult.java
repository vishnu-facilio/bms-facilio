package com.facilio.bmsconsole.interceptors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.json.JSONResult;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.filters.MultiReadServletRequest;
import com.facilio.fw.LRUCache;
import com.opensymphony.xwork2.ActionInvocation;

public class CacheResult extends JSONResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public void execute(ActionInvocation invocation)
            throws Exception
            {
			   super.execute(invocation);

            }
	protected void writeToResponse(javax.servlet.http.HttpServletResponse response,
            String json,
            boolean gzip)
                 throws IOException
                 {
		HttpServletRequest request =
				  ServletActionContext.getRequest();
		if (request instanceof MultiReadServletRequest && ((MultiReadServletRequest) request).isCachedRequest()) {
			MultiReadServletRequest multiReadServletRequest = (MultiReadServletRequest) request;
			String requestURI = multiReadServletRequest.getRequestURI();
	        String contentHash = multiReadServletRequest.getContentHash();
			long userId = AccountUtil.getCurrentUser().getId();
			long orgId = AccountUtil.getCurrentOrg().getId();
			
			LRUCache.getResponseCache().put(CacheUtil.RESPONSE_KEY(orgId, userId, requestURI, contentHash), json);
		}
		Object cacheurl = request.getAttribute("cacheurl");
		if(cacheurl !=null)
		{
			// TODO : save the JSON data in cache
		}
		else
		{
			//System.out.println("pls check the cache parameter in struts config file !!!");
		}
		super.writeToResponse(response,json,gzip);

					
                 }
}
