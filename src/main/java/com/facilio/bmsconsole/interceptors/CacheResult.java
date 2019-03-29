package com.facilio.bmsconsole.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.json.JSONResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
