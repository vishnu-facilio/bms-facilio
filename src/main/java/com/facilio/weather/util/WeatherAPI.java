package com.facilio.weather.util;

import lombok.extern.log4j.Log4j;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationUtil;
import com.facilio.relation.util.RelationshipDataUtil;
import com.facilio.service.FacilioService;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.ServiceHttpUtils;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.weather.bean.WeatherBean;
import com.facilio.weather.service.WeatherService;
import com.facilio.weather.service.WeatherServiceType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.json.simple.JSONObject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class WeatherAPI {
	private static String jobName = "FacilioWeatherJob";

	public static WeatherBean getWeatherBean(long orgId) throws IllegalAccessException, InstantiationException {
		return (WeatherBean) BeanFactory.lookup("WeatherBean", orgId);
	}

	public static void deleteWeatherServiceJob(V3WeatherServiceContext weatherServiceContext) throws Exception {
		long jobId = weatherServiceContext.getId();
		FacilioTimer.deleteJob(jobId, jobName);
	}

	public static void addWeatherServiceJob(V3WeatherServiceContext weatherServiceContext) {
		long jobId = weatherServiceContext.getId();
		try {
			JobContext jobContext = FacilioTimer.getJob(jobId, jobName);
			if(jobContext!=null) {
				deleteWeatherServiceJob(weatherServiceContext);
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
				.beanClass(V3WeatherStationContext.class);
		if(serviceId!=-1) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("serviceId"), String.valueOf(serviceId), NumberOperators.EQUALS));
		}
		return selectBuilder.get();
	}

	public static List<V3WeatherStationContext> getStations(String stationIds) throws Exception {
		ModuleBean modBean = Constants.getModBean();
		FacilioModule module = modBean.getModule(FacilioConstants.ModuleNames.WEATHER_STATION);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ModuleNames.WEATHER_STATION);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<V3WeatherStationContext> selectBuilder = new SelectRecordsBuilder<V3WeatherStationContext>()
				.select(fields)
				.module(module)
				.beanClass(V3WeatherStationContext.class)
				.andCondition(CriteriaAPI.getIdCondition(stationIds, module));
		return selectBuilder.get();
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

		return getStationCode(lat, lng, WeatherServiceType.getWeatherService(FacilioProperties.getConfig("weather.service"), ""));
	}

	public static JSONObject getStationCode(double lat, double lng, long serviceId) throws Exception {
		V3WeatherServiceContext serviceContext = WeatherAPI.getWeatherServiceById(serviceId);
		if(serviceId == 0){ //TODO - Seeni [weather]: backward compatability code. remove this after a week
			serviceContext = WeatherAPI.getWeatherServiceByDisplayName("Facilio Weather");
		}
		if(serviceContext == null) {
			throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, "Weather service not found for given service Id - "+serviceId );
		}
		WeatherService weatherService = WeatherServiceType.getWeatherService(serviceContext.getName().toString(), serviceContext.getApiKey());
		return getStationCode(lat, lng, weatherService);

	}

	public static JSONObject getStationCode(double lat, double lng, WeatherService weatherService) throws Exception {
		return weatherService.getStationCode(lat, lng);
	}

	public static Map<String, Object> getWeatherData(WeatherService service, V3WeatherStationContext weatherStation, Long time) throws Exception {
		String stationCode = weatherStation.getStationCode();
		if (StringUtils.isEmpty(stationCode)) {
			return null;
		}
		return service.getWeatherData(weatherStation, time, true, WeatherAPI.isStartOfTheDay());
	}

	public static String doGet(String url) throws Exception {
		String weatherServiceRegion = "us-west-2";
		if(FacilioProperties.getRegion().equals("dev")) {
			weatherServiceRegion = "dev";
		}
		String response = ServiceHttpUtils.doHttpGet(weatherServiceRegion, FacilioConstants.Services.WEATHER_SERVICE, url, null, null);
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

	public static V3WeatherServiceContext getWeatherServiceById(long id) throws Exception {
		return V3RecordAPI.getRecord(FacilioConstants.ModuleNames.WEATHER_SERVICE, id);
	}

	public static V3WeatherServiceContext getWeatherServiceByDisplayName(String displayName) throws Exception {
		ModuleBean modBean = Constants.getModBean();
		FacilioModule module = modBean.getModule(FacilioConstants.ModuleNames.WEATHER_SERVICE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ModuleNames.WEATHER_SERVICE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<V3WeatherServiceContext> selectBuilder = new SelectRecordsBuilder<V3WeatherServiceContext>()
				.select(fields)
				.module(module)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("displayName"),displayName, StringOperators.IS))
				.beanClass(V3WeatherServiceContext.class);
		List<V3WeatherServiceContext> result = selectBuilder.get();
		if(CollectionUtils.isEmpty(result)) {
			return null;
		}
		return result.get(0);
	}

	public static V3WeatherStationContext getExistingWeatherStation(String stationCode, Long serviceId) throws Exception {
		ModuleBean modBean = Constants.getModBean();
		FacilioModule module = modBean.getModule(FacilioConstants.ModuleNames.WEATHER_STATION);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ModuleNames.WEATHER_STATION);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<V3WeatherStationContext> selectBuilder = new SelectRecordsBuilder<V3WeatherStationContext>()
				.select(fields)
				.module(module)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("serviceId"), String.valueOf(serviceId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("stationCode"), stationCode, StringOperators.IS))
				.beanClass(V3WeatherStationContext.class);
		List<V3WeatherStationContext> result = selectBuilder.get();
		if(CollectionUtils.isEmpty(result)) {
			return null;
		}
		return result.get(0);
	}

	public static void associateSiteWithWeatherStation(long siteId, long stationId) throws Exception {
		relationBetweenSiteAndWeatherStation(siteId, stationId, true);
	}

	public static void dissociateSiteWithWeatherStation(long siteId, long stationId) throws Exception {
		relationBetweenSiteAndWeatherStation(siteId, stationId, false);
	}

	public static void relationBetweenSiteAndWeatherStation(long siteId, long stationId, boolean enable) throws Exception {
		Map<String, List<Object>> queryParameters = new HashMap<>();
		queryParameters.put("relationName", new ArrayList(){{
			add("belongsto");
		}});
		queryParameters.put("parentId", new ArrayList(){{
				add(siteId);
			}});
		Map<String,Object> relationValue = new HashMap();
		List<Long> idlist = new ArrayList();
		idlist.add(stationId);
		relationValue.put("weatherstation", idlist);
		if(enable) {
			RelationshipDataUtil.associateRelation("site", FieldUtil.getAsJSON(relationValue), queryParameters, null);
		} else {
			RelationshipDataUtil.dissociateRelation("site", FieldUtil.getAsJSON(relationValue), queryParameters, null);
		}
	}

	public static long getStationIdForSiteId(Long siteId) throws Exception {
		String revLinkName = "beingusedby";
		String requiredModuleName = "weatherstation";
		JSONObject recordsWithRelationship = RelationUtil.getRecordsWithRelationship(revLinkName, requiredModuleName, siteId, -1, -1);
		JSONObject data = (JSONObject) recordsWithRelationship.get("data");
		if(data != null) {
			List<Map> resources = (ArrayList<Map>) data.get(requiredModuleName);
			if(!resources.isEmpty()) {
				return (long) resources.get(0).get("id");
			}
		}
		return 0;
	}

	public static List<Long> getSiteIdsForStationId(Long stationId) throws Exception {
		String revLinkName = "belongsto";
		String requiredModuleName = "site";
		JSONObject recordsWithRelationship = RelationUtil.getRecordsWithRelationship(revLinkName, requiredModuleName, stationId, -1, -1);
		JSONObject data = (JSONObject) recordsWithRelationship.get("data");
		if(data != null) {
			List<Map> resources = (ArrayList<Map>) data.get(requiredModuleName);
			if(!resources.isEmpty()) {
				return resources.stream().map(row -> (long)row.get("id")).collect(Collectors.toList());
			}
		}
		return new ArrayList<>();
	}

	public static boolean allow() throws Exception {
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WEATHER_INTEGRATION)) {
			String env = FacilioProperties.getEnvironment();
			if (FacilioProperties.isProduction() || FacilioProperties.isDevelopment() || env.equals("stage2")) {
				return true;
			}
			List<Long> allowedStageOrgs = new ArrayList() {{
				add(905L);
				add(1280L);
			}};
			return FacilioProperties.getEnvironment().equals("stage")
					&& allowedStageOrgs.contains(AccountUtil.getCurrentOrg().getOrgId());
		}
		return false;
	}

	public static Map<Long, List<Map<String, Object>>> getWeatherReadings() throws Exception {
		return WeatherUtil.getReadings(FacilioConstants.ContextNames.NEW_WEATHER_READING);
	}

	public static boolean isStartOfTheDay() {
		return DateTimeUtil.getCurrenTime(true) == DateTimeUtil.getDayStartTime(true);
	}

	public static Long getTimeinMillis(Object time) {
		return time != null ? (Long) time * 1000 : null;
	}

	public final static Long ROUND_OFF_MIN = (long) (5 * 60 * 1000); //5 mins
	public static Long roundOfTime(Object time) {
		long ttime = (long) time;
		ttime /= ROUND_OFF_MIN;
		ttime *= ROUND_OFF_MIN;
		return ttime;
	}

	public static JSONObject formattedResponse(List<Map<String, Object>> weatherData) {
		Map<String, Object> currentMap = null;
		List<Map<String, Object>> hourlyList = new ArrayList<>();
		for(Map map : weatherData) {
			Long ttime = (Long) map.get("ttime");
			if(ttime.equals(map.get("predictedTime"))) {
				currentMap = map;
				currentMap.put("time", ttime/1000);
			} else {
				map.put("time", ttime/1000);
				hourlyList.add(map);
			}
		}

		JSONObject hourlyObj = new JSONObject();
		hourlyObj.put("data", hourlyList);
		JSONObject formattedResponse = new JSONObject();
		formattedResponse.put("currently", currentMap);
		formattedResponse.put("forecast", hourlyObj);
		return formattedResponse;
	}

}
