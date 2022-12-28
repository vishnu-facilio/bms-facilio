package com.facilio.weather.actions;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;
import com.facilio.weather.commands.WeatherReadOnlyChainFactory;
import lombok.Setter;
import org.json.simple.JSONObject;

@Setter
public class WeatherAction extends V3Action {
    private static final long serialVersionUID = 1L;
    private Double lat;
    private Double lng;
    private long stationId;
    private long siteId;

    public String getStationCode() throws Exception {
        FacilioChain chain = WeatherReadOnlyChainFactory.getWeatherStationChain();
        FacilioContext context = chain.getContext();
        context.put("LAT", lat);
        context.put("LNG", lng);
        chain.execute();
        setData((JSONObject) context.get("STATION_DATA"));
        return V3Action.SUCCESS;
    }

    public String getCurrentWeather() throws Exception {
        FacilioChain chain = WeatherReadOnlyChainFactory.getCurrentWeatherChain();
        FacilioContext context = chain.getContext();
        context.put("STATION_ID", stationId);
        context.put("SITE_ID", siteId);
        chain.execute();
        setData((JSONObject) context.get("data"));
        if(context.get("CODE")!=null) {
            setCode((Integer) context.get("CODE"));
            setMessage((String) context.get("MESSAGE"));
        }
        return V3Action.SUCCESS;
    }

    public String getAssociatedStationId() throws Exception {
        FacilioChain chain = WeatherReadOnlyChainFactory.getSiteWeatherStationChain();
        FacilioContext context = chain.getContext();
        context.put("SITE_ID", siteId);
        chain.execute();
        setData("stationId", context.get("STATION_ID"));
        if(context.get("MESSAGE")!=null) {
            setMessage((String) context.get("MESSAGE"));
        }
        return V3Action.SUCCESS;
    }
}
