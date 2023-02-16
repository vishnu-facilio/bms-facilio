package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.weather.util.WeatherAPI;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.weather.context.WeatherStationContext;

public class DailyWeatherStationJob extends FacilioJob {
	private static final Logger logger = LogManager.getLogger(DailyWeatherStationJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {

		try {
			if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_SITE_SUMMARY) || !WeatherAPI.allow()) {
				return;
			}
			List<WeatherStationContext> weatherStations = WeatherUtil.getAllWeatherStations();
			Map<Long, Map<Long, List<Long>>> orgWeatherMap = WeatherUtil.getOrgVsSiteVsWeatherStation();
			Map<Long, Map<String, Object>> allWeatherDataMaps = WeatherUtil.getWeatherDataMap(weatherStations,DateTimeUtil.getDayStartTime(-1) / 1000);
			for (Entry<Long, Map<Long, List<Long>>> entry : orgWeatherMap.entrySet()) {
				long orgId = entry.getKey();
				ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
				bean.addOrUpdateDailyWeatherData(entry.getValue(), allWeatherDataMaps);
			}

		} catch (Exception e) {
			logger.log(Level.ERROR, e.getMessage(), e);
			CommonCommandUtil.emailException("WeatherStationHourlyJob", "Exception in WeatherStationHourlyJob  ", e);
		}
	}
}
