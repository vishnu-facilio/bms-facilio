package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.weather.service.WeatherService;
import com.facilio.weather.service.WeatherServiceType;
import com.facilio.weather.util.WeatherAPI;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class FacilioWeatherDataJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		int noOfStations = 0;  // remove these later
		int noOfReadings = 0;
		try {
			//logger.log(Level.INFO,"The weather data feature enabled for orgid: "+AccountUtil.getCurrentOrg().getOrgId());
			if (!WeatherAPI.allow()) {
				return;
			}

			List<V3WeatherStationContext> stations = WeatherAPI.getAllStations(jc.getJobId());
			List<ReadingContext> psychrometricReadings= new ArrayList<>();
			Map<Long,List<ReadingContext>> stationCurrentReadings = new HashMap<>();

			V3WeatherServiceContext weatherServiceContext =
					(V3WeatherServiceContext) RecordAPI.getRecord(FacilioConstants.ModuleNames.WEATHER_SERVICE, jc.getJobId());
			WeatherService weatherService = WeatherServiceType.getWeatherService(weatherServiceContext.getName());

			for(V3WeatherStationContext station : stations) {
				Map<String,Object> weatherData = WeatherAPI.getWeatherData(weatherService, station, null);
				LOGGER.debug("The weather data: "+weatherData);
				if(weatherData==null || weatherData.isEmpty()) {
					continue;
				}
				long parentId = station.getId();
				Map<String,Object> currentWeather= (Map<String, Object>) weatherData.get("currently");
				ReadingContext reading = WeatherUtil.getHourlyReadingOld(parentId ,FacilioConstants.ContextNames.NEW_WEATHER_READING,currentWeather);
				if(reading!=null) {
					WeatherUtil.populateMap(parentId , reading, stationCurrentReadings);
					ReadingContext psychrometricReading = WeatherUtil.getPsychrometricReading(parentId , currentWeather);
					if(psychrometricReading != null) {
						psychrometricReadings.add(psychrometricReading);
					}
					LOGGER.debug("The psychometric data: "+psychrometricReading);
				}
				//forecast..
				List<ReadingContext> hourlyForecast= WeatherUtil.getHourlyForecastReadings(parentId ,FacilioConstants.ContextNames.WEATHER_HOURLY_FORECAST_READING,weatherData,true);
				if(!hourlyForecast.isEmpty()) {
					WeatherUtil.populateMap(parentId , hourlyForecast, stationCurrentReadings);

				}
				WeatherUtil.addDewPoint(parentId, stationCurrentReadings);
			}
			WeatherUtil.addReading(FacilioConstants.ContextNames.NEW_PSYCHROMETRIC_READING ,psychrometricReadings);

			stationCurrentReadings = WeatherUtil.getWeatherReading(FacilioConstants.ContextNames.NEW_WEATHER_READING, stationCurrentReadings);
			List<ReadingContext> newWeatherReadings = WeatherUtil.getReadingList(stationCurrentReadings);
			noOfStations = stationCurrentReadings.size();
			noOfReadings = newWeatherReadings.size();
			WeatherUtil.addReading(FacilioConstants.ContextNames.NEW_WEATHER_READING,newWeatherReadings);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			CommonCommandUtil.emailException("FacilioWeatherDataJob", "Exception in Facilio Weather Data job ", e);
		} finally {
			if(noOfStations != 0) {
				LOGGER.info("WEATHER_LOG :: No of weather stations :: "+noOfStations);
				LOGGER.info("WEATHER_LOG :: No of weather points going to be updated :: "+noOfReadings);
			}
		}
	}
}
