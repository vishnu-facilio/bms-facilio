package com.facilio.fw.filter;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FacilioUriFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    private static final String URL_PATTERN = "/api/";
    private static final String AUTH_API = "/auth/api/";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (StringUtils.isEmpty((CharSequence) request.getAttribute(IAMAppUtil.REQUEST_APP_NAME))) {
            String reqUri = request.getRequestURI();
            if (!reqUri.startsWith(AUTH_API)) {
                int idx = reqUri.indexOf(URL_PATTERN);
                if (idx > 0) {
                    String appName = reqUri.substring(1, idx);
                    String newUri = reqUri.substring(idx);
                    System.out.println("Facilio filter called : " + newUri);
                    request.setAttribute(IAMAppUtil.REQUEST_APP_NAME, appName);
                    req.getRequestDispatcher(newUri).forward(request, response);
                    System.out.println("Req completed");
                } else {
                    chain.doFilter(request, response);
                }
            }
            else {
                chain.doFilter(request, response);
            }
        }
        else {
            System.out.println("Else of facilio filter : "+request.getAttribute(IAMAppUtil.REQUEST_APP_NAME));
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
