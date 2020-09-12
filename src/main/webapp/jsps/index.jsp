<!DOCTYPE html>
<%@page import="com.facilio.auth.cookie.FacilioCookie"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@ page import="org.json.simple.JSONObject" %>
<%@page import="com.facilio.iam.accounts.util.IAMAppUtil" %>
<%@page import="java.util.Map" %>
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

	JSONObject domainInfo = IAMAppUtil.getAppDomainType(request.getServerName());
	
	if (isSutherland) {
		copyrightInfo.put("name", "Sutherland Global Services, Inc");
		copyrightInfo.put("year", "2020");
		
		rebrandInfo.put("brandName", "Sutherland");
		rebrandInfo.put("name", "Sutherland");
		rebrandInfo.put("domain", "sutherlandglobal.com");
		rebrandInfo.put("copyright", copyrightInfo);
	}
	
	String userAgent = request.getHeader("User-Agent");
	boolean isWindows = (userAgent != null && userAgent.indexOf("Windows") >= 0) ? true : false;
%>
<html>

<head>
    <meta charset="utf-8">
    <meta name="format-detection" content="telephone=no">
    <meta name="msapplication-tap-highlight" content="no">
    <meta name="google-site-verification" content="ASK Altair" />
    <meta name="viewport" content="user-scalable=no,initial-scale=1,maximum-scale=1,minimum-scale=1,width=device-width">
    <title>
    		<% if(isBuildingstalk) {%>
    			BuildingsTalk
    		<% } else if(isSutherland) {%>
    			Sutherland
    		<%} else { %>
    			Facilio
    		<%} %>
    </title>

<% if(isBuildingstalk) {%>
	<link rel="icon" href="<%=staticUrl%>/statics/machinestalk.ico" type="image/x-icon">
<% } else if(isSutherland) {%>
	<link rel="icon" href="<%=staticUrl%>/statics/sutherland.ico" type="image/x-icon">
<% } else {%>
	<link rel="icon" href="<%=staticUrl%>/statics/favicon.png" type="image/x-icon">
<% }%>
  <!--<link rel="icon" href="<%=staticUrl%>/statics/favicon.png" type="image/x-icon">
    <link rel="icon" href="<%=staticUrl%>/statics/machinestalk.ico" type="image/x-icon"> -->

    <link rel="manifest" href="<%=staticUrl%>/statics/manifest.json">

    <style>
      .app-spinner {
        width: 70px;
        text-align: center;
        position: absolute;
        left: 0;
        top: 0;
        right: 0;
        bottom: 0;
        margin: auto;
        height: 22px;
      }

      .app-spinner > div {
        width: 18px;
        height: 18px;
        background-color: #2F2E49;
        border-radius: 100%;
        display: inline-block;
        -webkit-animation: sk-bouncedelay 1.4s infinite ease-in-out both;
        animation: sk-bouncedelay 1.4s infinite ease-in-out both;
      }

      .app-spinner .bounce1 {
        -webkit-animation-delay: -0.32s;
        animation-delay: -0.32s;
      }

      .app-spinner .bounce2 {
        -webkit-animation-delay: -0.16s;
        animation-delay: -0.16s;
      }

      @-webkit-keyframes sk-bouncedelay {
        0%, 80%, 100% { -webkit-transform: scale(0) }
        40% { -webkit-transform: scale(1.0) }
      }

      @keyframes sk-bouncedelay {
        0%, 80%, 100% {
          -webkit-transform: scale(0);
          transform: scale(0);
        } 40% {
          -webkit-transform: scale(1.0);
          transform: scale(1.0);
        }
      }

  </style>
  
  <% if (isWindows) { %>
  	<style>
 		::-webkit-scrollbar {
       width: 8px;
       height: 20px;
	  display: block !important;
	}
	
 	::scrollbar {
    width: 8px;
	display: block !important;
    }
    
    ::-webkit-scrollbar-track {
      background: #fff; 
    }

    ::scrollbar-track {
      background: #fff; 
      background-clip: content-box;
    }

    ::-webkit-scrollbar-thumb {
      height: 10px;
	  background-color: rgba(68, 68, 68, 0.4);
      border-radius: 10px;
      -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
      box-shadow: inset 0 0 6px rgba(0,0,0,.3);
    }

	  ::-webkit-scrollbar-thumb:hover {
	    background-color: rgba(68, 68, 68, 0.6);
	  }

      ::scrollbar-thumb:hover {
        background-color: rgba(68, 68, 68, 0.6);
      }
      
      ::-webkit-scroll-thumb:vertical{
      	height: 10px;
      }
      
      ::scroll-thumb:vertical{
      	height: 10px;
      }
      
      ::-webkit-scrollbar:horizontal{
         height: 10px;
      }
      
      ::scrollbar:horizontal{
      	width: 10px;
      	height: 8px;
      }
      
	  ::-webkit-scrollbar:vertical{
	   height: 10px; 
	  }
  	</style>
  <% } %>

  <script type="text/javascript">
        window.isFacilioAuth = (document.cookie.indexOf('fc.authtype=facilio') !== -1)
        var webpackPublicPath = "<%=staticUrl%>";
        var servicePortalDomain = "<%=servicePortalDomain%>";
        var rebrandInfo = <%=rebrandInfo%>;
        window.rebrandInfo = rebrandInfo;
        var googleAuthEnable = <%=googleAuthEnable%>;
        var googleAuthClientId = "<%=googleAuthClientId%>";
        var domainInfo = <%=domainInfo%>;
  </script>
  
<link href="<%=staticUrl%>/css/chunk-vendors.css" rel="stylesheet">
<link href="<%=staticUrl%>/css/app.css" rel="stylesheet">
  
</head>
  <body>
      <div id="q-app">
          <div class="app-spinner">
              <div class="bounce1"></div>
              <div class="bounce2"></div>
              <div class="bounce3"></div>
          </div>
      </div>

      <script type="text/javascript" charset="UTF-8" src="<%=staticUrl%>/js/chunk-vendors.js"></script>
      <script type="text/javascript" charset="UTF-8" src="<%=staticUrl%>/js/app.js"></script>
      
  </body>
  <script src="https://apis.google.com/js/api:client.js"></script>
</html>