package com.facilio.fw.filter;

import com.facilio.util.RequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UriFilter implements Filter {
    private static final String URL_PATTERN = "/api/";

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

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Commenting out the if check because it shouldn't be called when app name is set
//        if (StringUtils.isEmpty((CharSequence) request.getAttribute(RequestUtil.REQUEST_APP_NAME))) {
            String reqUri = RequestUtil.normalize(request.getRequestURI(), true);
            if (isWhiteListedUri(reqUri)) {
                req.getRequestDispatcher(reqUri).forward(request, response); //Doing this to restrict direct hitting of struts2 urls
            }
            else if (!isAllowedExtension(reqUri)) {
                send404(response);
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
}
