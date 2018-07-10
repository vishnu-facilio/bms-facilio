package com.facilio.filters;


import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class IotFilter implements Filter {

    private static final Logger LOGGER = LogManager.getLogger(IotFilter.class.getName());
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        JSONParser parser = new JSONParser();
        JSONObject data = null;
        try {
            data = (JSONObject) parser.parse(request.getParameter("data"));
            LOGGER.info(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {

    }
}
