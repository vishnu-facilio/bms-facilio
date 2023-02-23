package com.facilio.weather.commands;

import com.facilio.weather.util.WeatherAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

public class GetSiteStationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long siteId = (long) context.get("siteId");
        V3Util.throwRestException(siteId == 0, ErrorCode.VALIDATION_ERROR, "siteId is needed");
        long stationId =  WeatherAPI.getStationIdForSiteId(siteId);
        if(stationId == 0) {
            stationId = -1;
            context.put("message", "given siteId is not associated with any weather station");
        }
        context.put("stationId", stationId);
        return false;
    }
}
