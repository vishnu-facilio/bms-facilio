package com.facilio.wmsv2.handler;


import com.facilio.weather.bean.WeatherBean;
import com.facilio.weather.util.WeatherAPI;
import com.facilio.wmsv2.message.Message;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
public class NewWeatherJobHandler extends BaseHandler {
    @Override
    public void processOutgoingMessage(Message message) {

        Long orgId = message.getOrgId();
        try {
            if(message!=null && message.getContent()!=null) {
                JSONObject content = message.getContent();
                WeatherBean weatherBean = WeatherAPI.getWeatherBean(orgId);
                weatherBean.addOrUpdateWeatherStationData(content);
            }
        } catch (Exception e) {
            LOGGER.error("WEATHER_ERROR :: ERROR IN [NewWeatherJobHandler] for ORGID "+ orgId
                    + " with topic :: "+message.getTopic(), e);
        }
    }

}
