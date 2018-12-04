<!DOCTYPE html>
<%
	String clientVersion = com.facilio.aws.util.AwsUtil.getClientVersion();
	if (clientVersion != null && !clientVersion.startsWith("/")) {
		clientVersion = "/" + clientVersion;
	} else {
	    clientVersion = "";
	}
	
	String staticUrl = com.facilio.aws.util.AwsUtil.getConfig("static.url") + clientVersion;
	String servicePortalDomain = com.facilio.aws.util.AwsUtil.getConfig("portal.domain");
%>
<html>

<head>
    <meta charset="utf-8">
    <meta name="format-detection" content="telephone=no">
    <meta name="msapplication-tap-highlight" content="no">
    <meta name="viewport" content="user-scalable=no,initial-scale=1,maximum-scale=1,minimum-scale=1,width=device-width">
    <title>Facilio</title>

    <link rel="icon" href="<%=staticUrl%>/statics/favicon.png" type="image/x-icon">
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
  </script>
  <link href="<%=staticUrl%>/app.css" rel="stylesheet">
 </head>

  <body>
      <div id="q-app">
          <div class="app-spinner">
              <div class="bounce1"></div>
              <div class="bounce2"></div>
              <div class="bounce3"></div>
          </div>
      </div>
      <script type="text/javascript" charset="UTF-8" src="<%=staticUrl%>/js/manifest.js"></script>
      <script type="text/javascript" charset="UTF-8" src="<%=staticUrl%>/js/vendor.js"></script>
      <script type="text/javascript" charset="UTF-8" src="<%=staticUrl%>/js/app.js"></script>
  </body>
  
  <script src="//cdn.quilljs.com/1.3.6/quill.min.js"></script>
  <link rel="stylesheet" href="//cdn.quilljs.com/1.3.6/quill.snow.css" />

  </html>