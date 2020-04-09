package com.facilio.fw.filter;

import com.facilio.auth.cookie.FacilioCookie;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
        urls.add("/api/v2/files/");
        urls.add("/api/v3/files/");
        urls.add("/websocket/");
        return urls;
    }

    private static final List<String> WHITE_LISTED_URIS = Collections.unmodifiableList(initWhiteListedUrls());

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String requestUri = request.getRequestURI();
        if (true /*FacilioProperties.isProduction()*/ && !isWhiteListedUri(requestUri)) {
            String csrfCookieToken = FacilioCookie.getUserCookie(request, FacilioCookie.CSRF_TOKEN_COOKIE);
            if (StringUtils.isNotEmpty(csrfCookieToken)) {
                String csrfHeaderToken = request.getHeader(CSRF_HEADER);
                if (csrfCookieToken.equals(csrfHeaderToken)) {
                    chain.doFilter(req, res);
                } else {
                    handleInvalid(request, response);
                    chain.doFilter(req, res);
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

    private void handleInvalid(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("send 403 for " + request.getServerName() + request.getRequestURI());
//        response.setContentType("text/plain");
//        response.setStatus(403);
//        response.resetBuffer();
    }

    @Override
    public void destroy() {

    }
}
