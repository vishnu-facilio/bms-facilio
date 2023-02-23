package com.facilio.weather.commands;

import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.weather.util.WeatherAPI;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllStationDetailsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3WeatherStationContext> stations = WeatherAPI.getAllStations(-1);
        context.put("stationMap", stations);
        return false;
    }
}
