package com.facilio.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

public class IotFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(IotFilter.class.getName());
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
            StringBuilder builder = new StringBuilder();
            String line = null;
            try {
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null)
                    builder.append(line);
            } catch (Exception e) {
                e.printStackTrace();
            }

            LOGGER.info(builder.toString());

    }

    @Override
    public void destroy() {

    }
}
