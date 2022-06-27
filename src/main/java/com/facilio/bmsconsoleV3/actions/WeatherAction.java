package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;
import lombok.Setter;
import org.json.simple.JSONObject;

@Setter
public class WeatherAction extends V3Action {
    private static final long serialVersionUID = 1L;
    private Double lat;
    private Double lng;

    public String getStationCode() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getWeatherStationChain();
        FacilioContext context = chain.getContext();
        context.put("LAT", lat);
        context.put("LNG", lng);
        chain.execute();
        setData((JSONObject) context.get("STATION_DATA"));
        return V3Action.SUCCESS;
    }
}
