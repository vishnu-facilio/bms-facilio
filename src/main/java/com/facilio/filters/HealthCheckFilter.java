package com.facilio.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HealthCheckFilter implements Filter {

    private static int status = HttpServletResponse.SC_SERVICE_UNAVAILABLE;

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setStatus(status);
    }

    public void destroy() {

    }

    public static void setStatus(int status) {
        HealthCheckFilter.status = status;
    }
}
