package com.facilio.bmsconsole.util;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.util.ServiceHttpUtils;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Level;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Log4j
public class WeatherAPI {

	private static String weatherURL = FacilioProperties.getConfig("weather.url");
	private static String jobName = "FacilioWeatherJob";

	public static void deleteWeatherJob(V3WeatherServiceContext weatherServiceContext) throws Exception {
		long jobId = weatherServiceContext.getId();
		FacilioTimer.deleteJob(jobId, jobName);
	}

	public static void addWeatherJob(V3WeatherServiceContext weatherServiceContext) {
		long jobId = weatherServiceContext.getId();
		try {
			JobContext jobContext = FacilioTimer.getJob(jobId, jobName);
			if(jobContext!=null) {
				deleteWeatherJob(weatherServiceContext);
			}
			long interval = weatherServiceContext.getDataInterval();
			LocalTime time = LocalTime.of(0, 0);
			ScheduleInfo scheduleInfo = new ScheduleInfo();
			scheduleInfo.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
			scheduleInfo.addTime(time);

			LocalTime nextTime = time.plusMinutes(interval);
			while(time.toSecondOfDay() < nextTime.toSecondOfDay()) {
				scheduleInfo.addTime(nextTime);
				time = nextTime;
				nextTime = nextTime.plusMinutes(interval);
			}

			FacilioTimer.scheduleCalendarJob(jobId, jobName, System.currentTimeMillis(), scheduleInfo, "facilio");
			LOGGER.info("Weather Service job "+jobName+" for jobid : " +jobId+" added successfully");
		} catch (Exception e) {
			String errMsg = "Error while adding "+jobName+" for jobid : " +jobId ;
			LOGGER.error(errMsg, e);
		}
	}

	public static List<V3WeatherStationContext> getAllStations(long serviceId) throws Exception {
		ModuleBean modBean = Constants.getModBean();
		FacilioModule module = modBean.getModule(FacilioConstants.ModuleNames.WEATHER_STATION);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ModuleNames.WEATHER_STATION);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<V3WeatherStationContext> selectBuilder = new SelectRecordsBuilder<V3WeatherStationContext>()
				.select(fields)
				.module(module)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("serviceId"), String.valueOf(serviceId), NumberOperators.EQUALS))
				.beanClass(V3WeatherStationContext.class);
		return selectBuilder.get();
	}

	public static V3WeatherServiceContext getWeatherService() {

		return null;
	}

	public static String getStationURL(double lat, double lng) {
		StringBuilder url = new StringBuilder(weatherURL);
		url.append("/stationcode?lat=");
		url.append(lat);
		url.append("&lng=");
		url.append(lng);
		LOGGER.log(Level.INFO, "Weather station url is : " + url);
		return  url.toString();
	}

	public static String getWeatherURL(long stationCode, Long time) {
		StringBuilder url = new StringBuilder(weatherURL);
		url.append("/data?stationCode=");
		url.append(stationCode);
		if (time != null) {
			url.append("&ttime=" + time);
		}
		LOGGER.log(Level.INFO, "Weather url is : " + url);
		return url.toString();
	}

	public static String getWeatherURL(double lat, double longitude, Long time) {
		StringBuilder url = new StringBuilder(weatherURL);
		url.append("/data?lat=");
		url.append(lat);
		url.append("&lng=");
		url.append(longitude);
		if (time != null) {
			url.append("&ttime=" + time);
		}
		LOGGER.log(Level.INFO, "Weather url is : " + url);
		return url.toString();
	}

	public static JSONObject getStationCode(LocationContext location, String siteName) throws Exception {
		if (location == null) {
			return null;
		}
		Double lat = location.getLat();
		Double lng = location.getLng();
		if (lat == null || lng == null || lat == -1 || lng == -1) {
			return null;
		}
		LOGGER.log(Level.INFO, " site: " + siteName + " lat: " + lat + " long: " + lng);
		return WeatherAPI.getStationCode(lat, lng);
	}

	public static JSONObject getStationCode(double lat, double lng) throws Exception {
		String weatherURL = WeatherAPI.getStationURL(lat, lng);
		return hitExternalWeatherService(weatherURL);
	}

	private static JSONObject hitExternalWeatherService(String url) throws Exception {
		String response = WeatherAPI.doGet(url);
		if (response == null || response.equals("")) {
			LOGGER.log(Level.INFO, "The response is null from the weather server");
			return null;
		}
		JSONObject weatherData = null;
		JSONParser parser = new JSONParser();
		try {
			JSONObject jsonResponse = (JSONObject) parser.parse(response);
			weatherData = (JSONObject) jsonResponse.get("data");
		} catch (Exception e) {
			throw new Exception(response, e);
		}
		return weatherData;
	}

	public static Map<String, Object> getWeatherData(V3WeatherStationContext weatherStation, Long time) throws Exception {
		long stationCode = weatherStation.getStationCode();
		if (stationCode == 0) {
			return null;
		}
		String weatherURL = WeatherAPI.getWeatherURL(stationCode, time);
		return hitExternalWeatherService(weatherURL);
	}

	public static String doGet(String url) throws Exception {
		if(!(FacilioProperties.getRegion().equals("us-west-2") || FacilioProperties.getRegion().equals("dev"))) {
			return null;
		}
		String response = ServiceHttpUtils.doHttpGet(FacilioProperties.getRegion(), FacilioConstants.Services.WEATHER_SERVICE, url, null, null);
		return response;
	}

	public static V3WeatherServiceContext getWeatherServiceByName(String name) throws Exception {
		ModuleBean modBean = Constants.getModBean();
		FacilioModule module = modBean.getModule(FacilioConstants.ModuleNames.WEATHER_SERVICE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ModuleNames.WEATHER_SERVICE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<V3WeatherServiceContext> selectBuilder = new SelectRecordsBuilder<V3WeatherServiceContext>()
				.select(fields)
				.module(module)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"),name, StringOperators.IS))
				.beanClass(V3WeatherServiceContext.class);
		List<V3WeatherServiceContext> result = selectBuilder.get();
		if(CollectionUtils.isEmpty(result)) {
			return null;
		}
		return result.get(0);
	}

	public static V3WeatherStationContext getWeatherStationByCode(long stationCode) throws Exception {
		ModuleBean modBean = Constants.getModBean();
		FacilioModule module = modBean.getModule(FacilioConstants.ModuleNames.WEATHER_STATION);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ModuleNames.WEATHER_STATION);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<V3WeatherStationContext> selectBuilder = new SelectRecordsBuilder<V3WeatherStationContext>()
				.select(fields)
				.module(module)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("stationCode"), String.valueOf(stationCode), NumberOperators.EQUALS))
				.beanClass(V3WeatherStationContext.class);
		List<V3WeatherStationContext> result = selectBuilder.get();
		if(CollectionUtils.isEmpty(result)) {
			return null;
		}
		return result.get(0);
	}

}
