package com.facilio.ims.handler;

import com.facilio.fms.message.Message;
import com.facilio.weather.bean.WeatherBean;
import com.facilio.weather.util.WeatherAPI;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
public class NewWeatherJobHandler extends ImsHandler {

    public static String KEY = "__newweather__";

    @Override
    public void processMessage(Message message) {

        Long orgId = message.getOrgId();
        try {
            if(message!=null && message.getContent()!=null) {
                JSONObject content = message.getContent();
                WeatherBean weatherBean = WeatherAPI.getWeatherBean(orgId);
                weatherBean.addOrUpdateWeatherStationData(content);
            }
        } catch (Exception e) {
            LOGGER.error("WEATHER_ERROR :: ERROR IN [NewWeatherJobHandler] for ORGID "+ orgId
                    + " with key :: "+message.getKey(), e);
        }
    }

}
