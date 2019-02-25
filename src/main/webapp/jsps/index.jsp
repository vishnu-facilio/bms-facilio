<!DOCTYPE html>
<%@ page import="org.json.simple.JSONObject" %>
<%
	String clientVersion = (String)com.facilio.aws.util.AwsUtil.getClientInfo().get("version");
	boolean isNewClientBuild=(Boolean)com.facilio.aws.util.AwsUtil.getClientInfo().get("isNewClientBuild");
	
	if (clientVersion != null && !clientVersion.startsWith("/")) {
		clientVersion = "/" + clientVersion;
	} else {
	    clientVersion = "";
	}
	
	String staticUrl = com.facilio.aws.util.AwsUtil.getConfig("static.url") + clientVersion;
	String servicePortalDomain = com.facilio.aws.util.AwsUtil.getConfig("portal.domain");
	
	String brandName = com.facilio.aws.util.AwsUtil.getConfig("rebrand.brand");
	String domain =com.facilio.aws.util.AwsUtil.getConfig("rebrand.domain"); 
	String copyrightName =com.facilio.aws.util.AwsUtil.getConfig("rebrand.copyright.name"); 
	String copyrightYear =com.facilio.aws.util.AwsUtil.getConfig("rebrand.copyright.year"); 
	
	JSONObject copyrightInfo = new JSONObject();
	copyrightInfo.put("name", copyrightName);
	copyrightInfo.put("year", copyrightYear);
	
	JSONObject rebrandInfo = new JSONObject();
	rebrandInfo.put("brandName", brandName);
	rebrandInfo.put("name", brandName);
	rebrandInfo.put("domain", domain);
	rebrandInfo.put("copyright", copyrightInfo);
%>
<html>

<head>
    <meta charset="utf-8">
    <meta name="format-detection" content="telephone=no">
    <meta name="msapplication-tap-highlight" content="no">
    <meta name="viewport" content="user-scalable=no,initial-scale=1,maximum-scale=1,minimum-scale=1,width=device-width">
    <title>Facilio</title>
<% 
if(brandName != null && (brandName.indexOf("BuildingsTalk") != -1 )) {%>
	<link rel="icon" href="<%=staticUrl%>/statics/machinestalk.ico" type="image/x-icon">
	
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

  <script type="text/javascript">
        window.isFacilioAuth = (document.cookie.indexOf('fc.authtype=facilio') !== -1)
        var webpackPublicPath = "<%=staticUrl%>";
        var servicePortalDomain = "<%=servicePortalDomain%>";
        var rebrandInfo = <%=rebrandInfo%>;
        window.rebrandInfo = rebrandInfo;
  </script>
  
<% if(isNewClientBuild) {%>
<link href="<%=staticUrl%>/css/chunk-vendors.css" rel="stylesheet">
<link href="<%=staticUrl%>/css/app.css" rel="stylesheet">
	
<% } else {%>
	<link href="<%=staticUrl%>/app.css" rel="stylesheet">	
<% }%>
  
 </head>

  <body>
      <div id="q-app">
          <div class="app-spinner">
              <div class="bounce1"></div>
              <div class="bounce2"></div>
              <div class="bounce3"></div>
          </div>
      </div>

<% if(isNewClientBuild) {%>
      <script type="text/javascript" charset="UTF-8" src="<%=staticUrl%>/js/chunk-vendors.js"></script>
      <script type="text/javascript" charset="UTF-8" src="<%=staticUrl%>/js/app.js"></script>
<% } else {%>
		
	  <script type="text/javascript" charset="UTF-8" src="<%=staticUrl%>/js/manifest.js"></script>
      <script type="text/javascript" charset="UTF-8" src="<%=staticUrl%>/js/vendor.js"></script>
      <script type="text/javascript" charset="UTF-8" src="<%=staticUrl%>/js/app.js"></script>
<% }%> 	
 
      
  </body>
  
  <script src="//unpkg.com/ckmeans@1.0.1/src/ckmeans.js"></script>
  <script src="//cdn.quilljs.com/1.3.6/quill.min.js"></script>
  <link rel="stylesheet" href="//cdn.quilljs.com/1.3.6/quill.snow.css" />

  </html>