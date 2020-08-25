package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import com.facilio.weather.context.WeatherStationContext;

public class AddOrUpdateDailyWeatherDataCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(AddOrUpdateDailyWeatherDataCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		try {
			Map<Long,List<Long>> siteVsWeatherStationMap = (Map<Long, List<Long>>) context.get("WeatherStationIdVsSiteId");
			Map<Long, Map<String, Object>> dataMap = (Map<Long, Map<String, Object>>) context.get("dataMap");
			Map<Long,List<ReadingContext>> siteDailyReadings = new HashMap<Long,List<ReadingContext>>();

			for(Entry<Long, List<Long>> entry:siteVsWeatherStationMap.entrySet()) {
				
				Map<String,Object> weatherData=dataMap.get(entry.getKey());
				LOGGER.info("WeatherStationDaily weather data : "+weatherData);
				if(MapUtils.isEmpty(weatherData)) {
					continue;
				}
				List<Long> sites = entry.getValue();
				for(long siteId:sites) {
					List<ReadingContext> readings= WeatherUtil.getDailyForecastReadings(siteId,FacilioConstants.ContextNames.WEATHER_DAILY_READING,weatherData,false);
					if(CollectionUtils.isNotEmpty(readings)) {
						WeatherUtil.populateMap(siteId, readings,siteDailyReadings);
					}
				}
			}
			siteDailyReadings   = WeatherUtil.getWeatherReading(FacilioConstants.ContextNames.WEATHER_DAILY_READING, siteDailyReadings );
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_DAILY_READING,WeatherUtil.getReadingList(siteDailyReadings));	
		}catch(Exception e) {
			LOGGER.error( e.getMessage(), e);
			CommonCommandUtil.emailException("WeatherStationDailyJob", "Exception in WeatherStationDailyJob ", e);
		}
		
		return false;
	}

	

}
