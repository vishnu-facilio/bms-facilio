package com.facilio.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.aws.util.AwsUtil;
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
    
    private static  HashMap customdomains ;

    private static String allowedHeaderString = "";
    private static String exposedHeaderString = "";
    private static String ip = "";

    private static final Logger LOGGER = LogManager.getLogger(FacilioCorsFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String originValues = AwsUtil.getConfig("cors.allowed.origins");
        if(originValues == null) {
            originValues = "https://facilio.ae,https://fazilio.com,https://facilio.com,https://facilio.in,https://facilstack.com,https://facilioportal.com";
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
        String corsRequestType = getCorsRequestType(request);
        
        if(customdomains==null) {
        	customdomains = (HashMap)(request.getServletContext()).getAttribute("customdomains");
        }

        if(ip != null) {
            response.addHeader("internal", ip);
        }

        response.setHeader(STRICT_TRANSPORT_SECURITY , "max-age=31556926; includeSubDomains");
        response.setHeader(X_FRAME_OPTIONS , "SAMEORIGIN");
        response.setHeader(X_XSS_PROTECTION , "1; mode=block");
        response.setHeader(X_CONTENT_TYPE_OPTIONS , "nosniff");
        response.setHeader( REFERRER_POLICY, "strict-origin-when-cross-origin");
        
        if (! (AwsUtil.isDevelopment() || AwsUtil.disableCSP())) {
        	response.setHeader(CONTENT_SECURITY_POLICY , "default-src https: data: self: 'unsafe-inline'; form-action https:; connect-src wss: https:; upgrade-insecure-requests");
        }
        response.setHeader(FEATURE_POLICY, "geolocation 'none'; autoplay 'none'");
        
        String forwardedProtocol = request.getHeader("X-Forwarded-Proto");
        if(forwardedProtocol != null) {
            if ("http".equalsIgnoreCase(forwardedProtocol)){
                response.sendRedirect("https://"+request.getServerName()+request.getRequestURI());
                return;
            }
        }

        switch (corsRequestType) {
            case CORS :
                handleCors(request, response, filterChain);
                break;
            case NO_CORS:
                filterChain.doFilter(request, response);
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
        if(!isRequestedMethodAllowed(requestedMethod)) {
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
            if(!isRequestedMethodAllowed(requestedMethod)) {
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
        LOGGER.info("send 403 for " + request.getRequestURI());
        response.setContentType("text/plain");
        response.setStatus(403);
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

    private boolean isRequestedMethodAllowed(String requestedMethod) {
        if(requestedMethod == null) {
            return false;
        }

        if(METHODS.isEmpty()){
            return true;
        }

        return METHODS.contains(requestedMethod.toLowerCase());
    }

    private String getCorsRequestType(HttpServletRequest request) {
        String requestType = INVALID;
        String originHeader = request.getHeader(ORIGIN);

        if(originHeader == null) {
            requestType = NO_CORS;
        } else {
            if(isAllowedOrigin(originHeader)) {
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

    private boolean isAllowedOrigin(String originHeader) {
        if(originHeader == null || originHeader.isEmpty()){
            return false;
        }

        if(ORIGINS.isEmpty()){
            return true;
        }

        String[] originHeaderdomain = originHeader.split("://");
        if (customdomains != null) {
			if (customdomains.containsKey(originHeaderdomain[1])) {
				return true;
			}
		}

        originHeader = originHeaderdomain[1];
        String[] domains = originHeader.split("\\.");
        String domain = originHeader;
        int domainLength = domains.length;
        if(domainLength == 4 && domain.contains(":")) {
            boolean ip = true;
            for(String octet : domains) {
                try {
                    if (octet.contains(":")) {
                        octet = octet.split(":")[0];
                    }
                    int i = Integer.parseInt(octet);
                    if ((i < 0) || (i > 255)) {
                        ip = false;
                    }
                } catch (Exception e) {
                    LOGGER.info("Exception in cors ", e);
                }
            }
            if(ip ) {
               domain = "https://" + domain;
            }
        } else if(domainLength > 2) {
            domain = "https://"+domains[domainLength-2]+"."+ domains[domainLength-1];
        }
        return ifDomainExists(domain);
    }

    private boolean ifDomainExists(String domain) {
        return ORIGINS.contains(domain.toLowerCase());
    }

    @Override
    public void destroy() {

    }
}
