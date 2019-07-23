package com.facilio.filters;


import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;


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