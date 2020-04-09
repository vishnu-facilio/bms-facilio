package com.facilio.fw.filter;

import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.fw.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class SecurityFilter implements Filter {
    private static final Logger LOGGER = LogManager.getLogger(SecurityFilter.class.getName());
    private static final String CSRF_HEADER = "x-csrf-token";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (true /*FacilioProperties.isProduction()*/) {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            String csrfCookieToken = FacilioCookie.getUserCookie(request, FacilioCookie.CSRF_TOKEN_COOKIE);
            if (StringUtils.isNotEmpty(csrfCookieToken)) {
                String csrfHeaderToken = request.getHeader(CSRF_HEADER);
                if (csrfCookieToken.equals(csrfHeaderToken)) {
                    chain.doFilter(req, res);
                } else {
                    handleInvalid(request, response);
                }
            } else {
                chain.doFilter(req, res);
            }
        }
        else {
            chain.doFilter(req , res);
        }
    }

    private void handleInvalid(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("send 403 for " + request.getServerName() + request.getRequestURI());
        response.setContentType("text/plain");
        response.setStatus(403);
        response.resetBuffer();
    }

    @Override
    public void destroy() {

    }
}
