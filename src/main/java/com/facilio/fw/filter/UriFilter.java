package com.facilio.fw.filter;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.filters.FacilioHttpRequest;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.util.RequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class UriFilter implements Filter {
    public static final String URL_PATTERN = "/api/";

    private List<String> whiteListedUris = null;
    private List<String> allowedExtensions = null;

    private List<String> parseConfig (String config) {
        if (StringUtils.isNotEmpty(config)) {
            List<String> values = new ArrayList<>();
            String[] split = config.split("\\s*,\\s*");
            for (String value : split) {
                values.add(value);
            }
            return Collections.unmodifiableList(values);
        }
        return null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String excludeUrls = filterConfig.getInitParameter("exclude");
        this.whiteListedUris = parseConfig(excludeUrls);
        String extensions = filterConfig.getInitParameter("allowed-extensions");
        this.allowedExtensions = parseConfig(extensions);
    }

    private boolean isWhiteListedUri (String uri) {
        if (CollectionUtils.isNotEmpty(whiteListedUris)) {
            for (String url : whiteListedUris) {
                boolean isWhiteListed = uri.startsWith(url);
                if (isWhiteListed) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAllowedExtension (String uri) {
        if (CollectionUtils.isNotEmpty(allowedExtensions)) {
            String extension = FilenameUtils.getExtension(uri);
            String fullName = FilenameUtils.getName(uri);
            String baseName = FilenameUtils.getBaseName(uri);

            if (StringUtils.isEmpty(extension)) {
                return fullName.equals(baseName); //Not allowing urls that end with .
            }
            else {
                return allowedExtensions.contains(extension);
            }
        }
        return true;
    }

    // For handling dynamic client builds
    private HttpServletRequest constructFacilioRequestIfNeeded (HttpServletRequest request) {
        if (!FacilioProperties.isProduction() && !FacilioProperties.isOnpremise()) {
            HttpServletRequest fRequest = parseAndConstructFacilioRequest(request, FacilioProperties.getStageDomains(), false);
            fRequest = fRequest == null ? parseAndConstructFacilioRequest(request, FacilioProperties.getPortalStageDomains(), true) : fRequest;
            if (fRequest != null) {
                return fRequest;
            }
        }
        return request;
    }

    private HttpServletRequest parseAndConstructFacilioRequest (HttpServletRequest request, List<String> stageDomains, boolean isPortal) {
        if (CollectionUtils.isNotEmpty(stageDomains)) {
            for (String stageDomain : stageDomains) {
                if (request.getServerName().endsWith(stageDomain)) {
                    if (request.getServerName().equals(stageDomain)) { //This is to handle main app stage domain
                        return request;
                    }
                    else {
                        String[] strings = request.getServerName().split("\\."); // There could be a better way to do this.
                        int noOfPartsInDomain = isPortal ? 4 : 3;
                        if (strings.length > noOfPartsInDomain) {
                            String clientBuild = strings[0];
                            StringJoiner serverName = new StringJoiner(".");
                            for (int i=1; i <= noOfPartsInDomain; i++) {
                                serverName.add(strings[i]);
                            }
                            return constructFacilioRequest (request, serverName.toString(), clientBuild);
                        }
                    }
                }
            }
        }
        return null;
    }

    private FacilioHttpRequest constructFacilioRequest (HttpServletRequest request, String serverName, String clientBuild) {
        request.setAttribute(RequestUtil.REQUEST_DYNAMIC_CLIENT_VERSION, clientBuild);
        return new FacilioHttpRequest(request, serverName);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = constructFacilioRequestIfNeeded((HttpServletRequest) req);
        HttpServletResponse response = (HttpServletResponse) res;
        // Commenting out the if check because it shouldn't be called when app name is set
//        if (StringUtils.isEmpty((CharSequence) request.getAttribute(RequestUtil.REQUEST_APP_NAME))) {
            String reqUri = FilenameUtils.normalize(request.getRequestURI(),true);
            if (!isAllowedExtension(reqUri)) {
                send404(response);
            }
            else if (isWhiteListedUri(reqUri)) {
                req.getRequestDispatcher(reqUri).forward(request, response); //Doing this to restrict direct hitting of struts2 urls
//            } else if (req.getServerName().equals(FacilioProperties.getDeveloperAppDomain())) {
//                String prefix = "/api/v1";
//                if (!reqUri.startsWith(prefix)) {
//                    send404(response);
//                }
//                request.getRequestDispatcher("/api/v4"+StringUtils.removeStart(reqUri, prefix)).forward(request, response);
//            }
            } else {
                String domainName = request.getServerName();

                // {orgdomain}.faciliosandbox.com/{sandbox}/{appname}/api
                if (SandboxAPI.isSandboxSubDomain(domainName)) {
                    if(reqUri.startsWith("/legacy")) {
                        // TODO - To be removed after changing URI structure in Client
                        chain.doFilter(request, response);
                        return;
                    }
                    List<String> reqUriSplit = Arrays.asList(reqUri.split("/"));
                    int idx = reqUriSplit.indexOf("api");
                    if (idx == 1) {                                                     // "/api/"
                        req.getRequestDispatcher(reqUri).forward(request, response);
                    } else if (idx == 2) {                                              // "/sandboxName/api/"
                        String sandboxName = reqUriSplit.get(1);
                        request.setAttribute(RequestUtil.ORG_SUBDOMAIN, sandboxName);
                        req.getRequestDispatcher(getApiURI(reqUri)).forward(request, response);
                    } else if (idx == 3){                                               // "/sandboxName/appName/api/"
                        String appName = reqUriSplit.get(2);
                        String sandboxName = reqUriSplit.get(1);
                        request.setAttribute(RequestUtil.ORG_SUBDOMAIN, sandboxName);
                        request.setAttribute(RequestUtil.REQUEST_APP_NAME, appName);
                        req.getRequestDispatcher(getApiURI(reqUri)).forward(request, response);
                    } else {                                                            // "/sandboxName/"
                        String sandboxName = reqUriSplit.get(1);
                        request.setAttribute(RequestUtil.ORG_SUBDOMAIN, sandboxName);

                        chain.doFilter(request, response);
                    }
                } else {
                    int idx = reqUri.indexOf(URL_PATTERN);
                    if (idx == 0) { //Doing this to make APIs called without app name to go through the usual security/ validation filters since we have struts2 configured to handle index.jsp globally
                        req.getRequestDispatcher(reqUri).forward(request, response); //Doing this to restrict direct hitting of struts2 urls
                    } else if (idx > 0) {
                        String newUri = reqUri.substring(idx);
                        String appName = reqUri.substring(1, idx);
                        System.out.println("Facilio filter called : " + newUri);
                        request.setAttribute(RequestUtil.REQUEST_APP_NAME, appName);
                        req.getRequestDispatcher(newUri).forward(request, response);
                    } else {
                        chain.doFilter(request, response);
                    }
                }

            }
//        }
//        else {
//            System.out.println("Else of facilio filter : "+request.getAttribute(RequestUtil.REQUEST_APP_NAME));
//            chain.doFilter(request, response);
//        }
    }

    private void send404(HttpServletResponse response) {
        response.setContentType("text/plain");
        response.setStatus(404);
        response.resetBuffer();
    }

    @Override
    public void destroy() {

    }

    private String getApiURI(String oldURI) {
        int idx = oldURI.indexOf(URL_PATTERN);
        return oldURI.substring(idx);
    }
}
