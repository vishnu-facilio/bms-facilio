package com.facilio.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class FacilioHttpRequest extends HttpServletRequestWrapper {
    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public FacilioHttpRequest (HttpServletRequest request) {
        super(request);
    }

    public FacilioHttpRequest (HttpServletRequest request, String serverName) {
        super(request);
        this.serverName = serverName;
    }

    private String serverName = null;

    @Override
    public String getServerName() {
        return serverName;
    }
}
