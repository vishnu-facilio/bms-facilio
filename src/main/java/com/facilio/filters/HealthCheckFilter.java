package com.facilio.filters;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class HealthCheckFilter implements Filter {

    private static int status = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
    private static String message = "app_server_running 0";

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setStatus(status);
        PrintWriter writer = response.getWriter();
        try {
            writer.println(message);
        } catch (Exception e){

        } finally {
            writer.close();
        }
    }

    public void destroy() {

    }

    public static void setStatus(int status) {
        HealthCheckFilter.status = status;
        if(status == 200) {
            message = "app_server_running 1";
        }
    }
}
