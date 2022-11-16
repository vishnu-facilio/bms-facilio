package com.facilio.weather.commands;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.WeatherAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class FetchWeatherStationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Double lat = (Double) context.get("LAT");
        Double lng = (Double) context.get("LNG");
        if(FacilioProperties.getEnvironment().startsWith("stage")) {
            return getRandomStationCode(lat, lng, context);
        }
        JSONObject stationData = WeatherAPI.getStationCode(lat, lng);
        context.put("STATION_DATA", stationData);
        return false;
    }

    private boolean getRandomStationCode(Double lat, Double lng, Context context) {
        JSONObject stationData = new JSONObject();
        long stationCode = System.currentTimeMillis()%10 + 1;
        stationData.put("stationCode", stationCode);
        stationData.put("lat", lat);
        stationData.put("lng", lng);
        stationData.put("identifier", "Area "+stationCode);
        stationData.put("country", "DC");
        context.put("STATION_DATA", stationData);
        return false;
    }
}
