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
<%@page import="java.util.concurrent.ConcurrentHashMap" %>
<%@page import="com.facilio.aws.util.FacilioProperties" %>
<%@page import="org.apache.commons.lang3.StringUtils" %>
<%@page import="com.facilio.util.FacilioIndexJsp" %>
<%@page import="com.facilio.util.RequestUtil" %>
<%@ page import="com.facilio.accounts.dto.AppDomain" %>

<%@page contentType="text/html; charset=UTF-8" %>

<%! static Map<String, String> indexHtmls = new ConcurrentHashMap<>(); %> <%-- Maybe change this to LRU Cache later --%>

<%

    try {
        boolean isDynamicClient = false;
        String clientVersion = (String) request.getAttribute(RequestUtil.REQUEST_DYNAMIC_CLIENT_VERSION);
        if (!FacilioProperties.isProduction()
                && StringUtils.isNotEmpty(clientVersion)
        ) {
            // String subDomain = request.getServerName().substring(0, request.getServerName().indexOf(FacilioProperties.getStageDomain()) - 1);
            System.out.println("Client Version => "+clientVersion);
            isDynamicClient = true;
        }
        else {
            clientVersion = (String) com.facilio.aws.util.AwsUtil.getClientInfo().get("version");
        }

        if (clientVersion != null && !clientVersion.startsWith("/")) {
            clientVersion = "/" + clientVersion;
        } else {
            clientVersion = "";
        }

        String staticUrlPropName = isDynamicClient ? "stage.static.url" : "static.url";
        String staticUrl = com.facilio.aws.util.FacilioProperties.getConfig(staticUrlPropName) + clientVersion;
        boolean servicePortalDomain = false;//used in client rendering to identify if the current req server is portal domain or not
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
        boolean isIungoCitGroup = request.getServerName().contains("iungo.citgroupltd"); 
		
        boolean isEcholtech = request.getServerName().contains("echoltech.com");

        boolean isStageTest = request.getServerName().contains("samltest.facilio.in");

        JSONObject domainInfo = IAMAppUtil.getAppDomainInfo(request.getServerName());

        if(domainInfo != null && domainInfo.containsKey("servicePortalDomain")) {
            servicePortalDomain = (Boolean)domainInfo.get("servicePortalDomain");
        }

        if (isSutherland) {
            copyrightInfo.put("name", "Sutherland Global Services, Inc");
            copyrightInfo.put("year", "2020");

            rebrandInfo.put("brandName", "Sutherland");
            rebrandInfo.put("name", "Sutherland");
            rebrandInfo.put("domain", "sutherlandglobal.com");
            rebrandInfo.put("copyright", copyrightInfo);
        }

        if (isEcholtech) {
            rebrandInfo.put("brandName", "Echol");
            rebrandInfo.put("name", "Echol");
            rebrandInfo.put("domain", "echoltech.com");
            rebrandInfo.put("copyright", copyrightInfo);
        }

        if (isStageTest) {
            rebrandInfo.put("brandName", "Echol");
            rebrandInfo.put("name", "Echol");
            rebrandInfo.put("domain", "echoltech.com");
            rebrandInfo.put("copyright", copyrightInfo);
        }
		if(isIungoCitGroup)
		{
			rebrandInfo.put("brandName", "iungo CIT Group Ltd");
            rebrandInfo.put("name", "iungoCitGroup");
            rebrandInfo.put("domain", "iungo.citgroupltd.com");
			
		}
        String userAgent = request.getHeader("User-Agent");
        String title = isIungoCitGroup ? "iungo CIT Group Ltd":isBuildingstalk ? "BuildingsTalk" : isSutherland ? "Sutherland" : (isEcholtech || isStageTest) ? "Echol" : "Facilio";
        String faviconPath = isIungoCitGroup ? "/statics/citgroup.ico":isBuildingstalk ? "/statics/machinestalk.ico" : isSutherland ? "/statics/sutherland.ico" : (isEcholtech || isStageTest) ? "/statics/echoltech-ico.png" : "/statics/favicon.png";
        

        Map<String, String> placeHolderParams = new HashMap<>();
        placeHolderParams.put("domainInfo", domainInfo.toString());
        placeHolderParams.put("rebrandInfo", rebrandInfo.toString());
        placeHolderParams.put("staticURL", staticUrl);
        placeHolderParams.put("webpackPublicPath", staticUrl);
        placeHolderParams.put("title", title);
        placeHolderParams.put("favicon", faviconPath);
        placeHolderParams.put("servicePortalDomain", String.valueOf(servicePortalDomain));
        placeHolderParams.put("googleAuthEnable", Boolean.toString(googleAuthEnable));
        placeHolderParams.put("googleAuthClientId", googleAuthClientId);

        /* Fetch index.html from s3 and replace placeholders. For index.html contents refer
           index.hbs in client repo
        */
        String html = indexHtmls.get(staticUrl);
        if (html == null || html.isEmpty()) {
            // System.out.println("no cache");
            String indexUrl = staticUrl + "/index.html";
            try (InputStream inputStream = new URL(indexUrl).openStream())
            {
                html = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                indexHtmls.put(staticUrl, html);
            }
            catch (Exception e) {
                FacilioIndexJsp.LOGGER.error("Error occurred in index.jsp", e);
                if (isDynamicClient) {
                    out.println("Please check the client build version given in domain. ");
                }
                else {
                    out.println("Internal Server Error. Please try after sometime");
                }
            }
        }
        /* else {
            System.out.println("cache");
        } */

        if (html == null || html.isEmpty()) {
            FacilioIndexJsp.LOGGER.error("Error occurred while loading client index.html from index.jsp");
            out.println("Internal Server Error. Please try after sometime");
        }
        else {
            StringSubstitutor substitutor = new StringSubstitutor(placeHolderParams, "{{", "}}");
            html = substitutor.replace(html);
            out.println(html);
        }
    }
    catch (Exception e) {
        FacilioIndexJsp.LOGGER.error("Error occurred in index.jsp", e);
        out.println("Internal Server Error. Please try after sometime");
    }
%>