package com.facilio.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
