package com.facilio.weather.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.V3Action;
import com.facilio.weather.commands.WeatherReadOnlyChainFactory;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.message.Message;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.time.ZoneId;

@Setter
public class WeatherAction extends V3Action {
    private static final long serialVersionUID = 1L;
    private Double lat;
    private Double lng;
    private long serviceId;
    private long stationId;
    private long siteId;
    private long buildingId;

    private String emailAddress;

    public String getStationCode() throws Exception {
        FacilioChain chain = WeatherReadOnlyChainFactory.getWeatherStationChain();
        FacilioContext context = chain.getContext();
        context.put("lat", lat);
        context.put("lng", lng);
        context.put("serviceId", serviceId);
        chain.execute();
        setData((JSONObject) context.get("stationData"));
        return V3Action.SUCCESS;
    }

    public String getCurrentWeather() throws Exception {
        FacilioChain chain = WeatherReadOnlyChainFactory.getCurrentWeatherChain();
        FacilioContext context = chain.getContext();
        context.put("stationId", stationId);
        context.put("siteId", siteId);
        chain.execute();
        setData((JSONObject) context.get("data"));
        if(context.get("code")!=null) {
            setCode((Integer) context.get("code"));
            setMessage((String) context.get("message"));
        }
        return V3Action.SUCCESS;
    }

    public String getAssociatedStationId() throws Exception {
        FacilioChain chain = WeatherReadOnlyChainFactory.getSiteWeatherStationChain();
        FacilioContext context = chain.getContext();
        context.put("siteId", siteId);
        chain.execute();
        setData("stationId", context.get("stationId"));
        if(context.get("message")!=null) {
            setMessage((String) context.get("message"));
        }
        return V3Action.SUCCESS;
    }

    public String getBuildingSpecificWeatherFields() throws Exception {
        FacilioChain chain = WeatherReadOnlyChainFactory.getBuildingWeatherFieldsChain();
        FacilioContext context = chain.getContext();
        context.put("buildingId", buildingId);
        chain.execute();
        if(context.get("message") == null) {
            setData("buildingId", context.get("buildingId"));
            setData("stationId", context.get("stationId"));
            setData("siteId", context.get("siteId"));
            setData("fields", context.get("fields"));
        } else {
            setCode(-1);
            setMessage((String) context.get("message"));
        }
        return V3Action.SUCCESS;
    }

    public String getAllWeatherFields() throws Exception {
        FacilioChain chain = WeatherReadOnlyChainFactory.getAllWeatherFieldsChain();
        FacilioContext context = chain.getContext();
        chain.execute();
        setData("fields", context.get("fields"));
        setData("stationMap", context.get("stationMap"));
        return V3Action.SUCCESS;
    }

    public String runWeatherStationMigration()  {
        if(StringUtils.isEmpty(emailAddress)) {
            setMessage("Migration is not initiated. Please provide emailAddress as param.");
            return V3Action.SUCCESS;
        }
        long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
        JSONObject content = new JSONObject();
        content.put("emailAddress", emailAddress);
        content.put("methodName", "addBulkWeatherStationMigration");
        content.put("startTime", DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata"))+"");
        WmsBroadcaster.getBroadcaster().sendMessage(new Message()
                .setTopic(Topics.Tasks.longRunnningTasks+"/"+ DateTimeUtil.getCurrenTime())
                .setOrgId(orgId)
                .setContent(content));
        setMessage("We will email to the given email address ["+emailAddress+"] once the migration is done.");
        return V3Action.SUCCESS;
    }

}
