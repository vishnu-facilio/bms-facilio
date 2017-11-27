package com.facilio.bmsconsole.interceptors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.json.JSONResult;

import com.opensymphony.xwork2.ActionInvocation;

public class CacheResult extends JSONResult {

	@Override
	public void execute(ActionInvocation invocation)
            throws Exception
            {
			System.out.println("before result execution");
			   super.execute(invocation);
				System.out.println("after result execution");

            }
	protected void writeToResponse(javax.servlet.http.HttpServletResponse response,
            String json,
            boolean gzip)
                 throws IOException
                 {
		HttpServletRequest request =
				  ServletActionContext.getRequest();
		Object cacheurl = request.getAttribute("cacheurl");
		if(cacheurl !=null)
		{
			// TODO : save the JSON data in cache
		System.out.println("Cache for url : "+request.getAttribute("cacheurl")+"Writing data to "+json);
		}
		else
		{
			//System.out.println("pls check the cache parameter in struts config file !!!");
		}
		
					super.writeToResponse(response,json,gzip);
                 }
}
