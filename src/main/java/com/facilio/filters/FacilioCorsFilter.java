package com.facilio.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.facilio.util.RequestUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.fw.listener.FacilioContextListener;

public class FacilioCorsFilter implements Filter {

    private static final HashSet<String> ORIGINS = new HashSet<>();
    private static final HashSet<String> METHODS = new HashSet<>();
    private static final HashSet<String> ALLOWED_HEADERS = new HashSet<>();
    private static final HashSet<String> EXPOSED_HEADERS = new HashSet<>();

    private static final String CORS = "Cors";
    private static final String INVALID = "Invalid";
    private static final String PREFLIGHT = "Preflight";
    private static final String NO_CORS = "Nocors";

    private static final String ORIGIN = "Origin";

    private static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    private static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
    private static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
    private static final String X_FRAME_OPTIONS = "X-Frame-options";
    private static final String X_XSS_PROTECTION = "X-XSS-Protection";
    private static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
    private static final String REFERRER_POLICY = "Referrer-Policy";
    private static final String FEATURE_POLICY = "Feature-Policy";



    private static final boolean SUPPORTS_CREDENTIALS = true;

    private static String allowedHeaderString = "";
    private static String exposedHeaderString = "";
    private static String ip = "";

    private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    private static final Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);


    private static final Logger LOGGER = LogManager.getLogger(FacilioCorsFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String originValues = FacilioProperties.getConfig("cors.allowed.origins");
        if(originValues == null) {
            originValues = "https://facilio.com,https://facilioportal.com,https://faciliovendors.com,https://faciliotenants.com,https://facilioworkplace.com";
        }
        initialize(ORIGINS, originValues);
        initialize(METHODS, filterConfig.getInitParameter("cors.allowed.methods"));
        initialize(ALLOWED_HEADERS, filterConfig.getInitParameter("cors.allowed.headers"));
        initialize(EXPOSED_HEADERS, filterConfig.getInitParameter("cors.exposed.headers"));
        allowedHeaderString  = filterConfig.getInitParameter("cors.allowed.headers");
        if(allowedHeaderString == null){
            allowedHeaderString = "";
        }
        exposedHeaderString = filterConfig.getInitParameter("cors.exposed.headers");
        if(exposedHeaderString == null){
            exposedHeaderString = "";
        }
        setServerIp();
    }

    private void setServerIp() {
        ip = FacilioContextListener.getInstanceId();
    }

    private void initialize(HashSet<String> origins, String values) {
        if(values != null) {
            String[] valueArray = values.split(",");
            for(String val : valueArray) {
                origins.add(val.trim().toLowerCase());
            }
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String forwardedProtocol = request.getHeader(RequestUtil.X_FORWARDED_PROTO);
        if(forwardedProtocol != null) {
            if ("http".equalsIgnoreCase(forwardedProtocol)){
                response.sendRedirect("https://" +request.getServerName()+request.getRequestURI());
                return;
            }
        }
        response.setHeader(STRICT_TRANSPORT_SECURITY , "max-age=31556926; includeSubDomains");
        response.setHeader(X_FRAME_OPTIONS , "SAMEORIGIN");
        response.setHeader(X_XSS_PROTECTION , "1; mode=block");
        response.setHeader(X_CONTENT_TYPE_OPTIONS , "nosniff");
        response.setHeader(REFERRER_POLICY, "strict-origin-when-cross-origin");

        if( ! (FacilioProperties.isOnpremise() || FacilioProperties.isDevelopment())) {
            if (FacilioProperties.isProduction()) {
                response.setHeader(CONTENT_SECURITY_POLICY , "default-src 'self' data: 'unsafe-inline' 'unsafe-eval' https:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://*.facilio.com https://apis.google.com https://maps.googleapis.com https://ssl.gstatic.com https://www.google-analytics.com https://accounts.google.com https://*.pendo.io; child-src 'self' blob:  https:; worker-src 'self' blob:; style-src 'self' data: 'unsafe-inline' https://*.facilio.com https://*.googleapis.com https://accounts.google.com; connect-src wss: https: data:; object-src 'none'; form-action https:; upgrade-insecure-requests; img-src 'self' blob: data: https:;");
           } else {
                response.setHeader(CONTENT_SECURITY_POLICY , "default-src 'self' data: 'unsafe-inline' 'unsafe-eval' https:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://*.facilio.in  https://apis.google.com https://maps.googleapis.com https://ssl.gstatic.com https://www.google-analytics.com https://accounts.google.com https://*.pendo.io; child-src 'self' blob:  https:; worker-src 'self' blob:; style-src 'self' data: 'unsafe-inline' https://*.facilio.in https://*.googleapis.com https://accounts.google.com; connect-src wss: https: data:; object-src 'none'; form-action https:; upgrade-insecure-requests; img-src 'self' blob: data: https:;");
            }
        }
        response.setHeader(FEATURE_POLICY, "geolocation 'none'; autoplay 'none'");

        String corsRequestType = getCorsRequestType(request);
        switch (corsRequestType) {
            case CORS :
                handleCors(request, response, filterChain);
                break;
            case NO_CORS:
                filterChain.doFilter(request, response);
                // Commenting it out for external VAPT check
//                if (ip != null) {
//                    response.addHeader("internal", ip);
//                }
                break;
            case INVALID:
                handleInvalid(request, response);
                break;
            case PREFLIGHT:
                handlePreFlight(request, response);
                break;
        }

    }

    private void handleCors(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestedMethod = request.getMethod();
        requestedMethod = requestedMethod.trim();
        if(isRequestedMethodNotAllowed(requestedMethod)) {
            LOGGER.info("Method is not allowed: " + requestedMethod);
            handleInvalid(request, response);
        } else {

            if (ORIGINS.isEmpty()) {
                response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            } else {
                response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(ORIGIN));
            }

            if (SUPPORTS_CREDENTIALS) {
                response.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            }

            if (! EXPOSED_HEADERS.isEmpty()) {
                response.addHeader("Access-Control-Expose-Headers", exposedHeaderString);
            }

            response.addHeader("Vary", "Origin");
            filterChain.doFilter(request, response);
        }

    }

    private void handlePreFlight(HttpServletRequest request, HttpServletResponse response){
        String requestedMethod = request.getHeader(ACCESS_CONTROL_REQUEST_METHOD);
        if (requestedMethod == null) {
            handleInvalid(request, response);
        } else {
            requestedMethod = requestedMethod.trim();
            if(isRequestedMethodNotAllowed(requestedMethod)) {
                handleInvalid(request, response);
                return;
            }
            String requestedHeaders = request.getHeader(ACCESS_CONTROL_REQUEST_HEADERS);
            if (requestedHeaders != null && !requestedHeaders.trim().isEmpty()) {
                String[] headers = requestedHeaders.trim().split(",");
                for(String header : headers) {
                    if(!isRequestedHeaderAllowed(header)) {
                        handleInvalid(request, response);
                        return;
                    }
                }
            }

            if (ORIGINS.isEmpty()) {
                response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            } else {
                response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(ORIGIN));
            }

            if (SUPPORTS_CREDENTIALS) {
                response.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            }

            response.addHeader(ACCESS_CONTROL_ALLOW_METHODS, requestedMethod);
            if (! ALLOWED_HEADERS.isEmpty()) {
                response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, allowedHeaderString);
            }

        }
    }

    private void handleInvalid(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("send 403 for " + request.getServerName()+request.getRequestURI() + " with origin " + request.getHeader(ORIGIN));
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.resetBuffer();
    }


    private boolean isRequestedHeaderAllowed(String header) {
        if(header == null){
            return false;
        }

        if(ALLOWED_HEADERS.isEmpty()){
            return true;
        }

        return ALLOWED_HEADERS.contains(header.trim().toLowerCase());
    }

    private boolean isRequestedMethodNotAllowed(String requestedMethod) {
        if(requestedMethod == null) {
            return true;
        }

        if(METHODS.isEmpty()){
            return false;
        }

        return !METHODS.contains(requestedMethod.toLowerCase());
    }

    private String getCorsRequestType(HttpServletRequest request) {
        String requestType = INVALID;
        String originHeader = request.getHeader(ORIGIN);

        if(originHeader == null) {
            // this check is to avoid directly calling the server with external ip.
            String serverName = request.getServerName();

            // this value should be null if you are invoking directly from the machine or local development.
            String forwardedProtocol = request.getHeader(RequestUtil.X_FORWARDED_PROTO);
            if(forwardedProtocol == null || isAllowedOrigin(forwardedProtocol+"://"+serverName)) {
                requestType = NO_CORS;
            }
        } else {
        	if(isAllowedOrigin(originHeader) || request.getRequestURI().contains("/sso/") || request.getRequestURI().endsWith("/api/v2/module/data/customUpload") || request.getRequestURI().contains("/dsso/")) {
                String requestMethod = request.getMethod();
                if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
                    String accessMethod = request.getHeader("Access-Control-Request-Method");
                    if (accessMethod != null && !accessMethod.isEmpty()) {
                        requestType = PREFLIGHT;
                    } else if (accessMethod != null && accessMethod.isEmpty()) {
                        requestType = INVALID;
                    } else {
                        requestType = CORS;
                    }
                } else if ("POST".equals(requestMethod)) {
                    String contentType = request.getContentType();
                    if (contentType != null) {
                        requestType = CORS;
                    }
                } else {
                    requestType = CORS;
                }
            }
        }
        return requestType;
    }

    public boolean isValidInet4Address(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        Matcher matcher = IPv4_PATTERN.matcher(ip);
        return matcher.matches();
    }

    private boolean isAllowedOrigin(String originHeader) {
        if(originHeader == null || originHeader.isEmpty()) {
            return false;
        }

        if(ORIGINS.isEmpty()){
            return true;
        }

        if (FacilioProperties.isDevelopment() && !FacilioProperties.isOnpremise()) {
            return true;
        }

        String[] originHeaderDomain = originHeader.split("://");
        String protocol = originHeaderDomain[0];
        originHeader = originHeaderDomain[1];
        String[] domains = originHeader.split("\\.");
        String domain = originHeader;
        int domainLength = domains.length;
        if(isValidInet4Address(originHeader)) {
            domain = protocol+"://"+originHeader;
        } else {
            if(domainLength == 1 ) {
                domain = protocol+"://"+originHeader;
              } else {
                domain = protocol + "://";
                for (int i = 1; i < domainLength; i++) {
                  domain = domain + domains[i];
                  if (domainLength - i > 1) {
                    domain = domain + ".";
                  }
                }
            }
        }
        boolean domainExists = ifDomainExists(domain);
        if (!domainExists && FacilioProperties.isOnpremise()) {
        		// Debugging
            LOGGER.info("Domain not allowed: " + domain + ", Origins - " + ORIGINS);
        		return true;
        }
        return domainExists;
    }

    private boolean ifDomainExists(String domain) {
        return ORIGINS.contains(domain.toLowerCase());
    }

    @Override
    public void destroy() {

    }
}
