package com.facilio.fw.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;

public class RequestUtil {

	public static String HOSTNAME = null;
	public static String MOBILE_HOSTNAME = null;

	public static String getDomainName()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		String requestSubdomain = null;//serverName.replaceAll(HOSTNAME, "");
		String serverName = request.getServerName();
		if(serverName.endsWith(RequestUtil.HOSTNAME))
		{
			requestSubdomain = serverName.replaceAll(RequestUtil.HOSTNAME, "");
			 request.setAttribute("isMobile", false);
			 System.out.println("desktop");
		}
		else
		{
			requestSubdomain = serverName.replaceAll(RequestUtil.MOBILE_HOSTNAME, "");
			request.setAttribute("isMobile", true);
			 System.out.println("mobile");

		}
		return requestSubdomain;
	}
}
