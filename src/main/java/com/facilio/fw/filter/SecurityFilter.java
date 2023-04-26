package com.facilio.fw.filter;

import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecurityFilter implements Filter {
    private static final Logger LOGGER = LogManager.getLogger(SecurityFilter.class.getName());
    private static final String CSRF_HEADER = "x-csrf-token";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    private static List<String> initWhiteListedUrls() {
        List<String> urls = new ArrayList<>();
        urls.add("/api/v2/files");
        urls.add("/api/v2/service/files");
        urls.add("/api/v2/connectedApps/download");
        urls.add("/api/v3/files");
        urls.add("/api/v3/service/files");
        urls.add("/api/integ/loadWebView");
        urls.add("/websocket/");
        urls.add("/sso/");
        urls.add("/portalsso/");
        urls.add("/api/v2/module/data/customUpload");
        urls.add("/api/v2/agent/agentDownload");
        urls.add("/api/v2/agent/certificate");
        urls.add("/api/v2/agent/config");
        urls.add("/api/v2/connection/callBack");
        urls.add("/api/integ/authorizeproxyuser");
        urls.add("/api/v3/file/public");
        urls.add("/api/v1/setup/connectedapps/hosting/download");
        urls.add("/api/v2/orgsetup");
        return urls;
    }

    private static final List<String> WHITE_LISTED_URIS = Collections.unmodifiableList(initWhiteListedUrls());

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String requestUri = request.getRequestURI();
        if ((FacilioProperties.isProduction() || FacilioProperties.isSecurtiyFilterEnabled()) && !isWhiteListedUri(requestUri)) {
            String csrfCookieToken = FacilioCookie.getUserCookie(request, FacilioCookie.CSRF_TOKEN_COOKIE);
            if (StringUtils.isNotEmpty(csrfCookieToken)) {
                String csrfHeaderToken = request.getHeader(CSRF_HEADER);
                if (csrfHeaderToken == null) {
                    if (handleInvalid(request, response, "header token is null when cookie is not null")) {
                        chain.doFilter(req, res);
                    }
                }
                else if (csrfCookieToken.equals(csrfHeaderToken)) {
                    chain.doFilter(req, res);
                } else {
                    if (handleInvalid(request, response, "header token didn't match with cookie")) {
                        chain.doFilter(req, res);
                    }
                }
            } else {
                chain.doFilter(req, res);
            }
        }
        else {
            chain.doFilter(req , res);
        }
    }

    private boolean isWhiteListedUri (String uri) {
        for (String url : WHITE_LISTED_URIS) {
            boolean isWhiteListed = uri.contains(url);
            if (isWhiteListed) {
                return true;
            }
        }
        return false;
    }

    private static String IOS_DEVICE_TYPE = "ios";
    private static String ANDROID_DEVICE_TYPE = "android";
    private boolean handleInvalid(HttpServletRequest request, HttpServletResponse response, String reason) {
        String uri = request.getRequestURI();
        String app = request.getServerName();
        String appName = (String) request.getAttribute(RequestUtil.REQUEST_APP_NAME);
        if (StringUtils.isNotEmpty(appName)) {
            app += "/" + appName;
        }
        StringBuilder msg = new StringBuilder("Sending 403 for URL : ")
                            .append(uri)
                            .append(" from app ")
                            .append(app)
                            .append(" because ")
                            .append(reason)
                            ;
        LoggingEvent event = new LoggingEvent(LOGGER.getName(), LOGGER, Level.INFO, msg.toString(), null);
        RequestUtil.addRequestLogEvents(request, event);
        event.setProperty("app", app);

        LOGGER.callAppenders(event);

        String deviceType = event.getProperty(RequestUtil.DEVICE_TYPE);
        if (!IOS_DEVICE_TYPE.equals(deviceType) && !ANDROID_DEVICE_TYPE.equals(deviceType)) {
            response.setContentType("text/plain");
            response.setStatus(403);
            response.resetBuffer();
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void destroy() {

    }
}
