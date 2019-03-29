package com.facilio.filters;


import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;


public class IotDataFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(IotDataFilter.class.getName());

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String headerToken = request.getHeader("Authorization");
        StringBuilder builder = new StringBuilder();
        String line = null;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.lineSeparator());
        }
        LOGGER.warn("header : " + headerToken + "  data : " + builder.toString());
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {

    }
}