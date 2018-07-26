package com.facilio.filters;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccessLogFilter implements Filter {

    private static final Logger LOGGER = LogManager.getLogger(AccessLogFilter.class.getName());

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        long startTime = System.currentTimeMillis();
        LoggingEvent event = new LoggingEvent("", LOGGER.getParent(), Priority.INFO, "", null);
        event.setProperty("remoteIp", request.getHeader("X-Forwarded-For"));
        event.setProperty("method", request.getMethod());
        event.setProperty("uri", request.getRequestURI());
        event.setProperty("query", request.getQueryString());
        filterChain.doFilter(servletRequest, response);
        long timeTaken = System.currentTimeMillis()-startTime;
        event.setProperty("timetaken", String.valueOf(timeTaken/1000));
        event.setProperty("timeInMillis", String.valueOf(timeTaken));
        Appender appender = LOGGER.getAppender("AccessLogAppender");
        appender.doAppend(event);
    }

    public void destroy() {

    }
}
