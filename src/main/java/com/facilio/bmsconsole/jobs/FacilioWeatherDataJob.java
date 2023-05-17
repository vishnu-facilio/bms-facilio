package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.weather.service.WeatherService;
import com.facilio.weather.service.WeatherServiceType;
import com.facilio.weather.util.WeatherAPI;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.message.Message;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

@Log4j
public class FacilioWeatherDataJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		try {
			if (!WeatherAPI.allow()) {
				return;
			}
			List<V3WeatherStationContext> stations = WeatherAPI.getAllStations(jc.getJobId());
			long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
			V3WeatherServiceContext weatherServiceContext =
					(V3WeatherServiceContext) RecordAPI.getRecord(FacilioConstants.ModuleNames.WEATHER_SERVICE, jc.getJobId());
			WeatherService weatherService = WeatherServiceType.getWeatherService(weatherServiceContext.getName());

			for(V3WeatherStationContext station : stations) {
				Map<String, Object> weatherData = WeatherAPI.getWeatherData(weatherService, station, null);
				LOGGER.debug("The weather data: " + weatherData);
				if (weatherData == null || weatherData.isEmpty()) {
					continue;
				}
				JSONObject content = new JSONObject();
				content.put("weatherData", weatherData);
				content.put("stationId", String.valueOf(station.getId()));
				WmsBroadcaster.getBroadcaster().sendMessage(new Message()
						.setTopic(Topics.Weather.newWeatherJob + "/" + orgId + "/" + station.getId())
						.setOrgId(orgId)
						.setContent(content));
			}
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			CommonCommandUtil.emailException("FacilioWeatherDataJob", "Exception in Facilio Weather Data job ", e);
		}
	}

}
