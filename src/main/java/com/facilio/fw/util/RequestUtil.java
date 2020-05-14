package com.facilio.fw.util;

import javax.servlet.http.HttpServletRequest;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.poi.ss.formula.functions.T;
import org.apache.struts2.ServletActionContext;

public class RequestUtil {

	public static String HOSTNAME = null;
	public static String MOBILE_HOSTNAME = null;
	private static final String X_FORWARDED_FOR = "X-Forwarded-For";
	private static final String DUMMY_REMOTE_IP = "0.0.0.1";
	private static final String ORIGIN_HEADER = "Origin";
	private static final String REQUEST_METHOD = "req_method";
	private static final String REQUEST_PARAMS = "req_params";
	private static final String DEFAULT_QUERY_STRING = "-";
	private static final String DEFAULT_ORG_USER_ID = "-1";
	private static final String X_DEVICE_TYPE = "X-Device-Type";
	private static final String X_APP_VERSION = "X-App-Version";
	private static final String ORIGIN = "origin";

	public static final String ORGID_HEADER = "orgId";
	public static final String USERID_HEADER = "userId";
	public static final String REQUEST_URL = "req_uri";
	public static final String REMOTE_IP = "remoteIp";
	public static final String REFERER = "referer";
	public static final String QUERY = "query";
	public static final String DEVICE_TYPE = "deviceType";

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

	private static String getRemoteAddr (HttpServletRequest request) {
		String remoteIp = request.getHeader(X_FORWARDED_FOR);
		if(remoteIp == null) {
			remoteIp = request.getRemoteAddr();
		}
		if(remoteIp == null) {
			remoteIp = DUMMY_REMOTE_IP;
		}
		return remoteIp;
	}

	private static String getOrigin (HttpServletRequest request) {
		String origin = request.getHeader(ORIGIN_HEADER);
		if(StringUtils.isEmpty(origin)) {
			origin = request.getServerName();
		}
		return origin;
	}

	public static void addRequestLogEvents (HttpServletRequest request, LoggingEvent event) {
		event.setProperty(REMOTE_IP, getRemoteAddr(request));
		event.setProperty(REQUEST_METHOD, request.getMethod());
		String requestUrl = request.getRequestURI();
		if("".equals(requestUrl)) {
			event.setProperty(REQUEST_URL, "/jsp/index.jsp");
		} else {
			event.setProperty(REQUEST_URL, requestUrl);
		}
		String referer = request.getHeader(HttpHeaders.REFERER);
		if (referer != null && !"".equals(referer.trim())) {
			event.setProperty(REFERER, referer);
		}
		String queryString = request.getQueryString();
		if(queryString == null) {
			queryString = DEFAULT_QUERY_STRING;
		}
		event.setProperty(QUERY, queryString);

		Organization org = AccountUtil.getCurrentOrg();
		String orgId = DEFAULT_ORG_USER_ID;
		if(org != null) {
			orgId = String.valueOf(org.getOrgId());
		}
		event.setProperty(ORGID_HEADER, orgId);

		User user = AccountUtil.getCurrentUser();
		String userId = DEFAULT_ORG_USER_ID;
		if (user != null) {
			userId = String.valueOf(user.getOuid());
		}
		event.setProperty(USERID_HEADER, userId);

		if (AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().getRequestParams() != null) {
			event.setProperty(REQUEST_PARAMS, AccountUtil.getCurrentAccount().getRequestParams());
		}

		event.setProperty(ORIGIN, RequestUtil.getOrigin(request));

		String deviceType = request.getHeader(X_DEVICE_TYPE);
		if(deviceType == null) {
			deviceType = DEFAULT_QUERY_STRING;
		}
		event.setProperty(DEVICE_TYPE, deviceType);

		String appVersion = request.getHeader(X_APP_VERSION);
		if(appVersion == null) {
			appVersion = DEFAULT_QUERY_STRING;
		}
		event.setProperty("appVersion", appVersion);
	}
}
