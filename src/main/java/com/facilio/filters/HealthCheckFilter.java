package com.facilio.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

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
