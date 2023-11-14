package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.NewWeatherJobHandler;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.weather.service.WeatherService;
import com.facilio.weather.service.WeatherServiceType;
import com.facilio.weather.util.WeatherAPI;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

@Log4j
public class FacilioWeatherDataJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		try {
			if (!WeatherAPI.allow()) {
				LOGGER.info("FacilioWeatherDataJob is not allowed to run");
				return;
			}
			V3WeatherServiceContext weatherServiceContext = V3RecordAPI.getRecord(FacilioConstants.ModuleNames.WEATHER_SERVICE, jc.getJobId());
			if(!weatherServiceContext.isStatus()) {
				LOGGER.info(weatherServiceContext.getName()+" - weather service is disabled");
				return;
			}
			List<V3WeatherStationContext> stations = WeatherAPI.getAllStations(jc.getJobId());
			LOGGER.info("No of stations :: "+stations.size());
			if(CollectionUtils.isEmpty(stations)) {
				return;
			}
			WeatherService weatherService =
					WeatherServiceType.getWeatherService(weatherServiceContext.getName().toString(), weatherServiceContext.getApiKey());
			if(weatherService == null) {
				return;
			}
			long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
			for(V3WeatherStationContext station : stations) {
				Map<String, Object> weatherData = WeatherAPI.getWeatherData(weatherService, station, null);
				LOGGER.debug("The weather data: " + weatherData);
				if (weatherData == null || weatherData.isEmpty()) {
					continue;
				}

				JSONObject forecastData = (JSONObject) weatherData.get("forecast");
				boolean first = true;
				if(forecastData != null) {
					List forecastArr = (List) forecastData.get("data");
					if(forecastArr!=null) {
						int arrSize = forecastArr.size();
						int end = 120;
						if(arrSize <= end) {
							pushToIms(weatherData, station.getId(), orgId);
							continue;
						}
						int start = 0 ;
						while(start < arrSize) {
							List subRecords = forecastArr.subList(start, Math.min(end, arrSize));
							JSONObject subWeatherData = new JSONObject();
							if(first) {
								subWeatherData.put("currently", weatherData.get("currently"));
							}
							JSONObject subForecast = new JSONObject();
							subForecast.put("data", subRecords);
							subWeatherData.put("forecast", subForecast);
							pushToIms(subWeatherData, station.getId(), orgId);
							start = end;
							end += 120;
							first = false;
						}
					}
				} else {
					pushToIms(weatherData, station.getId(), orgId);
				}


			}
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			CommonCommandUtil.emailException("FacilioWeatherDataJob", "Exception in Facilio Weather Data job ", e);
		}
	}

	private void pushToIms(Map<String, Object> weatherData, long id, long orgId) {
		JSONObject content = new JSONObject();
		content.put("weatherData", weatherData);
		content.put("stationId", String.valueOf(id));
		Messenger.getMessenger().sendMessage(new Message()
				.setKey(NewWeatherJobHandler.KEY + "/" + orgId + "/" + id)
				.setOrgId(orgId)
				.setContent(content));
	}

}
