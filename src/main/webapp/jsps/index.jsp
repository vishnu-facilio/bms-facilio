<%@page import="com.facilio.auth.cookie.FacilioCookie"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="org.json.simple.JSONObject" %>
<%@page import="com.facilio.iam.accounts.util.IAMAppUtil" %>
<%@page import="java.util.Map" %>
<%@page import="java.io.InputStream" %>
<%@page import="org.apache.commons.io.IOUtils" %>
<%@page import="org.apache.commons.text.StringSubstitutor" %>
<%@page import="java.nio.charset.StandardCharsets" %>
<%@page import="java.net.URL" %>
<%@page import="java.util.HashMap" %>

<%@page contentType="text/html; charset=UTF-8" %>

<%
	String clientVersion = (String)com.facilio.aws.util.AwsUtil.getClientInfo().get("version");
	
	if (clientVersion != null && !clientVersion.startsWith("/")) {
		clientVersion = "/" + clientVersion;
	} else {
	    clientVersion = "";
	}
	
	String staticUrl = com.facilio.aws.util.FacilioProperties.getConfig("static.url") + clientVersion;
	String servicePortalDomain = com.facilio.aws.util.FacilioProperties.getOccupantAppDomain();
	String allowedPortalDomain = com.facilio.aws.util.FacilioProperties.getPortalAppDomains();

	if(StringUtils.isNotEmpty(allowedPortalDomain)){
		String[] portaldomains = allowedPortalDomain.split(",");
		if(portaldomains.length > 0){
			List<String> allowedPortalsList = Arrays.asList(portaldomains);
			String currentServer = request.getServerName();
			String currentPortalDomain = null;
			String[] currentDomainArray = currentServer.split("\\."); 
		    if (currentDomainArray.length > 2) { 
		        currentPortalDomain = currentDomainArray[1]+ "." + currentDomainArray[2];
		        if(allowedPortalsList.contains(currentPortalDomain)){
		        	servicePortalDomain = currentPortalDomain;
		        }
		    }
		}
	}

	String brandName = com.facilio.aws.util.FacilioProperties.getConfig("rebrand.brand");
	String domain =com.facilio.aws.util.FacilioProperties.getConfig("rebrand.domain"); 
	String copyrightName =com.facilio.aws.util.FacilioProperties.getConfig("rebrand.copyright.name"); 
	String copyrightYear =com.facilio.aws.util.FacilioProperties.getConfig("rebrand.copyright.year"); 
	
	boolean googleAuthEnable = "true".equalsIgnoreCase(com.facilio.aws.util.FacilioProperties.getConfig("google.auth"));
	String googleAuthClientId =com.facilio.aws.util.FacilioProperties.getConfig("google.auth.clientid");
	
	// set csrf token cookie
	FacilioCookie.setCSRFTokenCookie(request, response, false);
	
	JSONObject copyrightInfo = new JSONObject();
	copyrightInfo.put("name", copyrightName);
	copyrightInfo.put("year", copyrightYear);
	
	JSONObject rebrandInfo = new JSONObject();
	rebrandInfo.put("brandName", brandName);
	rebrandInfo.put("name", brandName);
	rebrandInfo.put("domain", domain);
	rebrandInfo.put("copyright", copyrightInfo);

	boolean isBuildingstalk = (request.getServerName().endsWith("buildingsoncloud.com")  || ( brandName != null && (brandName.indexOf("BuildingsTalk") != -1 )));
	
	boolean isSutherland = request.getServerName().contains("sutherlandglobal.com");

	JSONObject domainInfo = IAMAppUtil.getAppDomainInfo(request.getServerName());
	
	if (isSutherland) {
		copyrightInfo.put("name", "Sutherland Global Services, Inc");
		copyrightInfo.put("year", "2020");
		
		rebrandInfo.put("brandName", "Sutherland");
		rebrandInfo.put("name", "Sutherland");
		rebrandInfo.put("domain", "sutherlandglobal.com");
		rebrandInfo.put("copyright", copyrightInfo);
	}
	
	String userAgent = request.getHeader("User-Agent");
	String title = isBuildingstalk ? "BuildingsTalk" : isSutherland ? "Sutherland" : "Facilio";
	String faviconPath = isBuildingstalk ? "/statics/machinestalk.ico" : isSutherland ? "/statics/sutherland.ico" : "/statics/favicon.png";

    Map<String, String> placeHolderParams = new HashMap<>();
    placeHolderParams.put("domainInfo", domainInfo.toString());
    placeHolderParams.put("rebrandInfo", rebrandInfo.toString());
    placeHolderParams.put("staticURL", staticUrl);
    placeHolderParams.put("webpackPublicPath", staticUrl);
    placeHolderParams.put("title", title);
    placeHolderParams.put("favicon", faviconPath);
    placeHolderParams.put("servicePortalDomain", servicePortalDomain);
    placeHolderParams.put("googleAuthEnable", Boolean.toString(googleAuthEnable));
    placeHolderParams.put("googleAuthClientId", googleAuthClientId);

	/* Fetch index.html from s3 and replace placeholders. For index.html contents refer
	   index.hbs in client repo
	*/
	String indexUrl = staticUrl + "/index.html";

    try (InputStream inputStream = new URL(indexUrl).openStream())
    {
        String html = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        
        StringSubstitutor substitutor = new StringSubstitutor(placeHolderParams, "{{", "}}");

        html = substitutor.replace(html);
        out.println(html);
    }
    catch (Exception e) {
        out.println("Internal Server Error. Please try after sometime");
    }
%>