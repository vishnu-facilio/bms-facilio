<%@page import="com.facilio.auth.cookie.FacilioCookie"%>
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
<%@page import="com.facilio.util.FacilioIndexJsp" %>
<%@ page import="com.facilio.client.app.pojo.ClientAppInfo" %>
<%@ page import="com.facilio.client.app.util.ClientAppUtil" %>
<%@ page import="com.facilio.aws.util.FacilioProperties" %>
<%@page contentType="text/html; charset=UTF-8" %>

<%! static Map<String, String> indexHtmls = new ConcurrentHashMap<>(); %> <%-- Maybe change this to LRU Cache later --%>

<%

    try {
        ClientAppInfo info = ClientAppUtil.getClientBuildInfo(request);
        String staticUrl = info.getStaticUrl();

        // Code after this needs to be refactored sometime
        boolean servicePortalDomain = false;//used in client rendering to identify if the current req server is portal domain or not
        String brandName = com.facilio.aws.util.FacilioProperties.getConfig("rebrand.brand");
        String domain = com.facilio.aws.util.FacilioProperties.getConfig("rebrand.domain");
        String copyrightName = com.facilio.aws.util.FacilioProperties.getConfig("rebrand.copyright.name");
        String copyrightYear = com.facilio.aws.util.FacilioProperties.getConfig("rebrand.copyright.year");

        boolean googleAuthEnable = "true".equalsIgnoreCase(com.facilio.aws.util.FacilioProperties.getConfig("google.auth"));
        String googleAuthClientId = com.facilio.aws.util.FacilioProperties.getConfig("google.auth.clientid");
        String datadogClientID = com.facilio.aws.util.FacilioProperties.getDatadogClientID();
        String identityServerURL = com.facilio.aws.util.FacilioProperties.getIdentityServerURL();
        boolean isGoogleAnalytics = false;
        boolean isProductionEnabled = FacilioProperties.isProduction();
        boolean isOnpremise = FacilioProperties.isOnpremise();
        if(isProductionEnabled == true && isOnpremise == false){
        isGoogleAnalytics = true;
        }

        // set csrf token cookie
        FacilioCookie.setCSRFTokenCookie(request, response, true);

        JSONObject copyrightInfo = new JSONObject();
        copyrightInfo.put("name", copyrightName);
        copyrightInfo.put("year", copyrightYear);

        JSONObject rebrandInfo = new JSONObject();
        rebrandInfo.put("brandName", brandName);
        rebrandInfo.put("name", brandName);
        rebrandInfo.put("domain", domain);
        rebrandInfo.put("copyright", copyrightInfo);
        rebrandInfo.put("servername", request.getServerName());

        boolean isBuildingstalk = (request.getServerName().endsWith("buildingsoncloud.com")  || ( brandName != null && (brandName.indexOf("BuildingsTalk") != -1 )));

        boolean isSutherland = request.getServerName().contains("sutherlandglobal.com");
        boolean isIungoCitGroup = request.getServerName().contains("iungo.citgroupltd"); 
		
        boolean isEcholtech = request.getServerName().contains("echoltech.com");

        boolean isMoro = (request.getServerName().endsWith("morodigital.com")  || ( brandName != null && (brandName.indexOf("Moro") != -1 )));

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

        if (isMoro) {
            rebrandInfo.put("brandName", "Moro");
            copyrightInfo.put("year", "2022");
            rebrandInfo.put("name", "Moro");
            rebrandInfo.put("domain", "morohub.com");
            rebrandInfo.put("copyright", copyrightInfo);
        }

        if (isBuildingstalk) {
            rebrandInfo.put("brandName", "BuildingsTalk");
            copyrightInfo.put("year", "2022");
            rebrandInfo.put("name", "Machinestalk Inc");
            rebrandInfo.put("domain", "buildingsoncloud.com");
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
        if (isMoro) { 
        	faviconPath = "/statics/moro-fav.png";
            title = "Moro";
        }

        JSONObject brandConfig = com.facilio.accounts.util.AccountUtil.getBrandingJson(request.getServerName());

        Map<String, String> placeHolderParams = new HashMap<>();
        placeHolderParams.put("brandConfig", brandConfig.toString());
        placeHolderParams.put("domainInfo", domainInfo.toString());
        placeHolderParams.put("rebrandInfo", rebrandInfo.toString());
        placeHolderParams.put("staticURL", staticUrl);
        placeHolderParams.put("webpackPublicPath", staticUrl);
        placeHolderParams.put("title", (String) brandConfig.get("name"));
        placeHolderParams.put("favicon", faviconPath);
        placeHolderParams.put("faviconURL", (String) brandConfig.get("favicon"));
        placeHolderParams.put("servicePortalDomain", String.valueOf(servicePortalDomain));
        placeHolderParams.put("googleAuthEnable", Boolean.toString(googleAuthEnable));
        placeHolderParams.put("googleAuthClientId", googleAuthClientId);
        placeHolderParams.put("dataDogClientId", datadogClientID);
        placeHolderParams.put("identityServerURL", identityServerURL);
        placeHolderParams.put("isGoogleAnalytics", Boolean.toString(isGoogleAnalytics));

        String isExport = request.getHeader("X-Is-Export");

        if(isExport != null && isExport.equals("true")) {
            placeHolderParams.put("dataDogClientId", "");
            placeHolderParams.put("isGoogleAnalytics", "false");
            FacilioIndexJsp.LOGGER.info("Disabling tracking scripts for puppeteer page load.");
        }

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
                if (info.isDynamicClient()) {
                    out.println("Please check the client build version given in domain. ");
                }
                else {
                    out.println("Internal Server Error. Please try after sometime");
                }
                response.setStatus(response.SC_INTERNAL_SERVER_ERROR);
            }
        }
        /* else {
            System.out.println("cache");
        } */

        if (html == null || html.isEmpty()) {
            FacilioIndexJsp.LOGGER.error("Error occurred while loading client index.html from index.jsp");
            out.println("Internal Server Error. Please try after sometime");
            response.setStatus(response.SC_INTERNAL_SERVER_ERROR);
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
        response.setStatus(response.SC_INTERNAL_SERVER_ERROR);
    }
%>