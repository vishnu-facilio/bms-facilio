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

<%@page contentType="text/html; charset=UTF-8" %>

<%! static Map<String, String> indexHtmls = new ConcurrentHashMap<>(); %> <%-- Maybe change this to LRU Cache later --%>

<%

    try {
        String clientVersion = null;
        boolean isDynamicClient = false;
        if (!FacilioProperties.isProduction()
                && StringUtils.isNotEmpty(FacilioProperties.getStageDomain())
                && !request.getServerName().equals(FacilioProperties.getStageDomain())
                && request.getServerName().endsWith(FacilioProperties.getStageDomain())
        ) {
            String subDomain = request.getServerName().substring(0, request.getServerName().indexOf(FacilioProperties.getStageDomain()) - 1);
            // System.out.println("Sub domain => "+subDomain);
            clientVersion = subDomain;
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

        boolean isEcholtech = request.getServerName().contains("echoltech.com");

        boolean isStageTest = request.getServerName().contains("samltest.facilio.in");

        JSONObject domainInfo = IAMAppUtil.getAppDomainInfo(request.getServerName());

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

        String userAgent = request.getHeader("User-Agent");
        String title = isBuildingstalk ? "BuildingsTalk" : isSutherland ? "Sutherland" : (isEcholtech || isStageTest) ? "Echol" : "Facilio";
        String faviconPath = isBuildingstalk ? "/statics/machinestalk.ico" : isSutherland ? "/statics/sutherland.ico" : (isEcholtech || isStageTest) ? "/statics/echoltech-ico.png" : "/statics/favicon.png";

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