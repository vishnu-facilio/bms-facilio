package com.facilio.weather.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.weather.util.WeatherAPI;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class FetchWeatherStationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Double lat = (Double) context.get("lat");
        Double lng = (Double) context.get("lng");

        if(!WeatherAPI.allow()) {
            return getRandomStationCode(lat, lng, context);
        }
        JSONObject stationData = WeatherAPI.getStationCode(lat, lng);
        context.put("stationData", stationData);
        return false;
    }

    private boolean getRandomStationCode(Double lat, Double lng, Context context) {
        JSONObject stationData = new JSONObject();
        long stationCode = System.currentTimeMillis()%10 + 1;
        stationData.put("stationCode", String.valueOf(stationCode));
        stationData.put("lat", lat);
        stationData.put("lng", lng);
        stationData.put("identifier", "Area "+stationCode);
        stationData.put("country", "DC");
        context.put("stationData", stationData);
        return false;
    }
}
