package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.weather.util.WeatherAPI;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class DegreeDaysCalculatorJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		try {
			if (!WeatherAPI.allow()) {
				return;
			}
			Map<Long, ReadingContext> readingMap = new HashMap<> ();

			//cdd n hdd calculations..
			Map<Long,List<Map<String,Object>>> stationVsReadings = WeatherAPI.getWeatherReadings();
			Set<Long> stationIds = stationVsReadings.keySet();
			if (stationIds!=null && !stationIds.isEmpty()) {
				readingMap = getWeatherReadings(stationVsReadings, stationIds);
			}

			//wet  bulb calculation..
			stationVsReadings = WeatherUtil.getWetBulbReadings();
			stationIds = stationVsReadings.keySet();
			if (stationIds!=null && !stationIds.isEmpty()) {
				getWetBulbReadings(stationVsReadings, stationIds, readingMap);
			}

			if(readingMap.isEmpty()) {
				return;
			}
			Map<String, List<ReadingContext>> moduleVsReading = new HashMap<>();
			moduleVsReading.put(FacilioConstants.ContextNames.DEGREE_DAY_READING,
					readingMap.values().stream().collect(Collectors.toList()));
			persistReading(moduleVsReading);

		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			CommonCommandUtil.emailException("DegreeDaysCalculatorJob", "Exception in calculating Degree Days", e);
		}
	}

	private void persistReading(Map<String, List<ReadingContext>> moduleVsReading) throws Exception {
		if(moduleVsReading.isEmpty()) {
			return;
		}

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.READINGS_MAP,moduleVsReading);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
		context.put(FacilioConstants.ContextNames.SKIP_PREV_READING_DATA_META, true);
		FacilioChain addDataChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
		addDataChain.execute(context);
	}

	private Map<Long, ReadingContext> getWeatherReadings(Map<Long, List<Map<String, Object>>> stationVsReadings,
														 Set<Long> stationIds) throws Exception {

		List<V3WeatherStationContext> stations = WeatherAPI.getStations(StringUtils.join(stationIds, ","));

		Map<Long, ReadingContext> readingMap = new HashMap<>();
		for(V3WeatherStationContext station: stations) {

			Long stationId = station.getId();
			Double cddBaseTemp = station.getCddBaseTemperature();
			Double hddBaseTemp = station.getHddBaseTemperature();
			List<Map<String,Object>> weatherReadings = stationVsReadings.get(stationId);

			if(cddBaseTemp>0) {
				Double cdd = WeatherUtil.getDegreeDays("cool","temperature",cddBaseTemp,weatherReadings);
				ReadingContext cReading = getReading(stationId,"cdd",cdd);
				addReading(stationId, cReading, readingMap);
			}

			if(hddBaseTemp>0) {
				Double hdd = WeatherUtil.getDegreeDays("heat","temperature",hddBaseTemp,weatherReadings);
				ReadingContext hReading= getReading(stationId,"hdd",hdd);
				addReading(stationId, hReading, readingMap);
			}

		}
		return readingMap;
	}

	private void addReading(Long stationId, ReadingContext reading, Map<Long, ReadingContext> stationVsReading) {
		ReadingContext existReading = stationVsReading.get(stationId);
		if(existReading == null) {
			existReading = reading;
		} else {
			for(Map.Entry<String, Object> en : reading.getReadings().entrySet() ) {
				existReading.addReading(en.getKey(), en.getValue());
			}
		}
		stationVsReading.put(stationId, existReading);
	}

	private void getWetBulbReadings(Map<Long, List<Map<String, Object>>> stationVsReading,
																 Set<Long> stationIds, Map<Long, ReadingContext> readingMap) throws Exception {

		List<V3WeatherStationContext> stations = WeatherAPI.getStations(StringUtils.join(stationIds, ","));
		if(readingMap == null) {
			readingMap = new HashMap<>();
		}

		for(V3WeatherStationContext station : stations) {

			Long stationId = station.getId();
			Double wddBaseTemp = station.getWddBaseTemperature();
			List<Map<String,Object>> weatherReadings = stationVsReading.get(stationId);
			if(wddBaseTemp>0) {
				Double wdd = WeatherUtil.getDegreeDays("wet","wetBulbTemperature",wddBaseTemp,weatherReadings);
				ReadingContext wReading= getReading(stationId,"wdd",wdd);
				addReading(stationId, wReading, readingMap);
			}
		}
	}

	private ReadingContext getReading(Long parentId,String key, Double dd) {

		ReadingContext reading= new ReadingContext();
		reading.setParentId(parentId);
		reading.setTtime(DateTimeUtil.getDayStartTime(-1));
		reading.addReading(key, dd);
		return reading;
	}
	
}
