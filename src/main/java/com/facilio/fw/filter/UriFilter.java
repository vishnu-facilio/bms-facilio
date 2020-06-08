package com.facilio.fw.filter;

import com.facilio.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UriFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    private static final String URL_PATTERN = "/api/";
    private static final String AUTH_API = "/auth/api/";
    private static final String IOT_API = "/iot/api/";
    private static final String MIGRATION_URI = "/internal/";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (StringUtils.isEmpty((CharSequence) request.getAttribute(RequestUtil.REQUEST_APP_NAME))) {
            String reqUri = request.getRequestURI();
            if (!reqUri.startsWith(AUTH_API) && !reqUri.startsWith(IOT_API) && !reqUri.startsWith(MIGRATION_URI)) {
                int idx = reqUri.indexOf(URL_PATTERN);
                if (idx == 0) { //Doing this to make APIs called without app name to go through the usual security/ validation filters since we have struts2 configured to handle index.jsp globally
                    req.getRequestDispatcher(reqUri).forward(request, response);
                } else if (idx > 0) {
                    String appName = reqUri.substring(1, idx);
                    String newUri = reqUri.substring(idx);
                    System.out.println("Facilio filter called : " + newUri);
                    request.setAttribute(RequestUtil.REQUEST_APP_NAME, appName);
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
            System.out.println("Else of facilio filter : "+request.getAttribute(RequestUtil.REQUEST_APP_NAME));
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
