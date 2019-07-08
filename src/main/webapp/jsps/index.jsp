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

<% if(brandName != null && (brandName.indexOf("BuildingsTalk") != -1 )) {%>
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

<% if (com.facilio.aws.util.AwsUtil.isProduction()) {%>
<!-- start Mixpanel --><script type="text/javascript">(function(c,a){if(!a.__SV){var b=window;try{var d,m,j,k=b.location,f=k.hash;d=function(a,b){return(m=a.match(RegExp(b+"=([^&]*)")))?m[1]:null};f&&d(f,"state")&&(j=JSON.parse(decodeURIComponent(d(f,"state"))),"mpeditor"===j.action&&(b.sessionStorage.setItem("_mpcehash",f),history.replaceState(j.desiredHash||"",c.title,k.pathname+k.search)))}catch(n){}var l,h;window.mixpanel=a;a._i=[];a.init=function(b,d,g){function c(b,i){var a=i.split(".");2==a.length&&(b=b[a[0]],i=a[1]);b[i]=function(){b.push([i].concat(Array.prototype.slice.call(arguments,
0)))}}var e=a;"undefined"!==typeof g?e=a[g]=[]:g="mixpanel";e.people=e.people||[];e.toString=function(b){var a="mixpanel";"mixpanel"!==g&&(a+="."+g);b||(a+=" (stub)");return a};e.people.toString=function(){return e.toString(1)+".people (stub)"};l="disable time_event track track_pageview track_links track_forms track_with_groups add_group set_group remove_group register register_once alias unregister identify name_tag set_config reset opt_in_tracking opt_out_tracking has_opted_in_tracking has_opted_out_tracking clear_opt_in_out_tracking people.set people.set_once people.unset people.increment people.append people.union people.track_charge people.clear_charges people.delete_user people.remove".split(" ");
for(h=0;h<l.length;h++)c(e,l[h]);var f="set set_once union unset remove delete".split(" ");e.get_group=function(){function a(c){b[c]=function(){call2_args=arguments;call2=[c].concat(Array.prototype.slice.call(call2_args,0));e.push([d,call2])}}for(var b={},d=["get_group"].concat(Array.prototype.slice.call(arguments,0)),c=0;c<f.length;c++)a(f[c]);return b};a._i.push([b,d,g])};a.__SV=1.2;b=c.createElement("script");b.type="text/javascript";b.async=!0;b.src="undefined"!==typeof MIXPANEL_CUSTOM_LIB_URL?
MIXPANEL_CUSTOM_LIB_URL:"file:"===c.location.protocol&&"//cdn4.mxpnl.com/libs/mixpanel-2-latest.min.js".match(/^\/\//)?"https://cdn4.mxpnl.com/libs/mixpanel-2-latest.min.js":"//cdn4.mxpnl.com/libs/mixpanel-2-latest.min.js";d=c.getElementsByTagName("script")[0];d.parentNode.insertBefore(b,d)}})(document,window.mixpanel||[]);
mixpanel.init("9247ee499df9581a488eefa6bd7bbaf5");</script><!-- end Mixpanel -->
<% } %>
  </body>
</html>