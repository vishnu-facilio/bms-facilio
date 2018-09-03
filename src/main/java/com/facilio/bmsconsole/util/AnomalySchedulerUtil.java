package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AnalyticsAnomalyConfigContext;
import com.facilio.bmsconsole.context.AnalyticsAnomalyContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TemperatureContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;


public class AnomalySchedulerUtil {
	private static final Logger logger = Logger.getLogger(AnomalySchedulerUtil.class.getName());
	private static final String energyDataTable = ModuleFactory.getAnalyticsAnomalyModule().getTableName();
	private static final String anomalyIdTable =  ModuleFactory.getAnalyticsAnomalyIDListModule().getTableName();
	private static final String weatherDataTable = ModuleFactory.getAnalyticsAnomalyModuleWeatherData().getTableName();
	private static final String anomalyConfigTable = ModuleFactory.getAnalyticsAnomalyConfigModule().getTableName();

	public static List<AnalyticsAnomalyConfigContext> getAllAssetConfigs(String moduleName, long orgID) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<AnalyticsAnomalyConfigContext> listOfReadings=new ArrayList<AnalyticsAnomalyConfigContext>();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getAnomalyConfigFields())
				.table(anomalyConfigTable)
				.andCustomWhere(" ORGID = ? ",  orgID);
	
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop:props) {
				AnalyticsAnomalyConfigContext anomalyConfigContext = FieldUtil.getAsBeanFromMap(prop, AnalyticsAnomalyConfigContext.class);
				listOfReadings.add(anomalyConfigContext);
			}
		}

		return listOfReadings;
	}
	
	public static List<AnalyticsAnomalyContext> getAllEnergyReadings(long startTime, long endTime, long meterID, long orgID)
		throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING));
		fields.add(modBean.getField("parentId", FacilioConstants.ContextNames.ENERGY_DATA_READING));
		fields.add(modBean.getField("ttime", FacilioConstants.ContextNames.ENERGY_DATA_READING));
		
		List<AnalyticsAnomalyContext> listOfReadings=new ArrayList<AnalyticsAnomalyContext>();
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(fields)
				.module(module)
				.andCustomWhere("TTIME >= ? AND TTIME < ? AND PARENT_METER_ID=? " + 
						" AND TOTAL_ENERGY_CONSUMPTION_DELTA IS NOT NULL  " + 
						" AND (MARKED is NULL OR MARKED = 0) ",
						startTime, endTime, meterID);
	
		List<Map<String, Object>> props = selectBuilder.getAsProps();
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