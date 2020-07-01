package com.facilio.fw.filter;

import com.facilio.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UriFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    private static final String URL_PATTERN = "/api/";
//    Shouldn't be needed as it doesn't match the url pattern
//    private static final String MIGRATION_URI = "/internal/";

    private static List<String> initWhiteListedUrls() {
        //The following urls won't be excluded. They will forwarded without app name parsing
        //If a url is added here, it needs to be handled in web.xml as well for struts2 to work. But that shouldn't be done.

        List<String> urls = new ArrayList<>();
        urls.add("/auth/api/");
        urls.add("/iot/api/");
        return urls;
    }

    private static final List<String> WHITE_LISTED_URIS = Collections.unmodifiableList(initWhiteListedUrls());

    private boolean isWhiteListedUri (String uri) {
        for (String url : WHITE_LISTED_URIS) {
            boolean isWhiteListed = uri.contains(url);
            if (isWhiteListed) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (StringUtils.isEmpty((CharSequence) request.getAttribute(RequestUtil.REQUEST_APP_NAME))) {
            String reqUri = RequestUtil.normalize(request.getRequestURI(), true);
            if (isWhiteListedUri(reqUri)) {
                req.getRequestDispatcher(reqUri).forward(request, response); //Doing this to restrict direct hitting of struts2 urls
            }
            else {
                int idx = reqUri.indexOf(URL_PATTERN);
                if (idx == 0) { //Doing this to make APIs called without app name to go through the usual security/ validation filters since we have struts2 configured to handle index.jsp globally
                    req.getRequestDispatcher(reqUri).forward(request, response); //Doing this to restrict direct hitting of struts2 urls
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
