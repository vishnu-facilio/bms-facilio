package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AnalyticsAnomalyContext;
import com.facilio.bmsconsole.context.TemperatureContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;


public class AnomalySchedulerUtil {
	

	private static final Logger logger = Logger.getLogger(AnomalySchedulerUtil.class.getName());
	private static final String energyDataTable = ModuleFactory.getAnalyticsAnomalyModule().getTableName();
	private static final String anomalyIdTable =  ModuleFactory.getAnalyticsAnomalyIDListModule().getTableName();
	private static final String weatherDataTable = ModuleFactory.getAnalyticsAnomalyModuleWeatherData().getTableName();
	
	public static List<AnalyticsAnomalyContext> getAllReadings(String moduleName, long startTime, long endTime, long meterID, long orgID) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<AnalyticsAnomalyContext> listOfReadings=new ArrayList<AnalyticsAnomalyContext>();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getAnomalySchedulerFields())
				.table(energyDataTable)
				.andCustomWhere("TTIME > ? AND TTIME < ? AND PARENT_METER_ID=? AND TOTAL_ENERGY_CONSUMPTION_DELTA IS NOT NULL  AND ORGID = ? ", startTime, endTime, meterID, orgID);
	
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop:props) {
				AnalyticsAnomalyContext anomalyContext = FieldUtil.getAsBeanFromMap(prop, AnalyticsAnomalyContext.class);
				listOfReadings.add(anomalyContext);
			}
		}

		return listOfReadings;
	}
	
	public static LinkedHashSet<Long> getExistingAnomalyIDs(String moduleName, String anomalyIDList) throws Exception{
		LinkedHashSet<Long> setOfAnomalyIDs=new LinkedHashSet<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getAnomalyIDFields())
				.table(anomalyIdTable)
				.andCustomWhere(" ID in  " + anomalyIDList);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop:props) {
				AnalyticsAnomalyContext context = FieldUtil.getAsBeanFromMap(prop, AnalyticsAnomalyContext.class);
				setOfAnomalyIDs.add(context.getId());
			}
		}

		return setOfAnomalyIDs;
	}
	
	public static List<TemperatureContext> getAllTemperatureReadings(String moduleName, long startTime, long endTime, 
			long orgID) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<TemperatureContext> listOfReadings=new ArrayList<TemperatureContext>();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getAnomalyTemperatureFields())
				.table("Weather_Reading")
				.andCustomWhere("Weather_Reading.TTIME > ? AND Weather_Reading.TTIME < ? AND Weather_Reading.TEMPERATURE IS NOT NULL AND Weather_Reading.ORGID = ? ", 
						(startTime - 45 * 60 * 1000L) , endTime, orgID);
	
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop:props) {
				TemperatureContext temperatureContext = FieldUtil.getAsBeanFromMap(prop, TemperatureContext.class);
				listOfReadings.add(temperatureContext);
			}
		}

		return listOfReadings;
	}
}