package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.util.WeatherAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class FetchWeatherStationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Double lat = (Double) context.get("LAT");
        Double lng = (Double) context.get("LNG");
        JSONObject stationData = WeatherAPI.getStationCode(lat, lng);
        context.put("STATION_DATA", stationData);
        return false;
    }
}
