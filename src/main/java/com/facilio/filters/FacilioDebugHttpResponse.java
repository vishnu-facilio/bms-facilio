package com.facilio.filters;

import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;

import javax.servlet.http.HttpServletResponse;

@Log4j
public class FacilioDebugHttpResponse extends FacilioHttpResponse {
    FacilioDebugHttpResponse(HttpServletResponse servletResponse) {
        super(servletResponse);
    }

    @Override
    public void setStatus(int sc) {
        if (sc == 500) {
            FacilioUtil.printTrace(LOGGER, "Setting status as 500 in response", 50);
        }
        super.setStatus(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        if (sc == 500) {
            FacilioUtil.printTrace(LOGGER, "Setting status as 500 in response", 50);
        }
        super.setStatus(sc, sm);
    }
}
