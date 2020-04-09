package com.facilio.fw.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

public class RequestUtil {

	public static String HOSTNAME = null;
	public static String MOBILE_HOSTNAME = null;
	private static final String X_FORWARDED_FOR = "X-Forwarded-For";
	private static final String DUMMY_REMOTE_IP = "0.0.0.1";

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

	public static String getRemoteAddr (HttpServletRequest request) {
		String remoteIp = request.getHeader(X_FORWARDED_FOR);
		if(remoteIp == null) {
			remoteIp = request.getRemoteAddr();
		}
		if(remoteIp == null) {
			remoteIp = DUMMY_REMOTE_IP;
		}
		return remoteIp;
	}
}
