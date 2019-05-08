package com.facilio.bmsconsole.util;


import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;


public class AnomalySchedulerUtil {
	private static final long ORG_LEVEL_METERID=-1;
	private static final Logger logger = Logger.getLogger(AnomalySchedulerUtil.class.getName());
	private static final String energyDataTable = ModuleFactory.getAnalyticsAnomalyModule().getTableName();
	private static final String anomalyIdTable =  ModuleFactory.getAnalyticsAnomalyIDListModule().getTableName();
	private static final String weatherDataTable = ModuleFactory.getAnalyticsAnomalyModuleWeatherData().getTableName();
	private static final String anomalyConfigTable = ModuleFactory.getAnalyticsAnomalyConfigModule().getTableName();


	public static Map<Long, AnalyticsAnomalyConfigContext> getAllAssetConfigWithDefaults(String moduleName, long orgID) throws Exception {
		List<AnalyticsAnomalyConfigContext> allConfigContexts = AnomalySchedulerUtil.getAllAssetConfigs(moduleName,orgID);

		Map<Long, AnalyticsAnomalyConfigContext> meterIdToConfigMap = new HashMap<>();
		for(AnalyticsAnomalyConfigContext eachMeterConfig: allConfigContexts) {
			meterIdToConfigMap.put(eachMeterConfig.getMeterId(), eachMeterConfig); 
		}
		
		return meterIdToConfigMap;
	}

	public static AnalyticsAnomalyConfigContext getAssetConfig(long meterID,  Map<Long, AnalyticsAnomalyConfigContext>  allMeterConfigs) throws Exception {
		long searchKey = ORG_LEVEL_METERID;
		if(allMeterConfigs.containsKey(meterID)) {
			searchKey = meterID;
		}
		return allMeterConfigs.get(searchKey);
	}
	
	public static List<EnergyMeterContext> getAllConfiguredEnergyMeters(Map<Long, AnalyticsAnomalyConfigContext> meterConfigurations) throws Exception{
		
		List<EnergyMeterContext>  requiredEnergyContexts = null;
		long configurationSize=meterConfigurations.size();
		
		if(configurationSize == 0) {
			return null;
		}
		
		if(meterConfigurations.containsKey(ORG_LEVEL_METERID)) {
				// no other meters configured
			requiredEnergyContexts = DeviceAPI.getAllEnergyMeters();
		}else {
			List<Long> meterIdList= new ArrayList<>();
			
			for(long eachMeter: meterConfigurations.keySet()) {
				if(eachMeter != ORG_LEVEL_METERID) {
					meterIdList.add(eachMeter);
				}
			}	
			
			// Only selected meters have been specified in the configuration
			String commaSeparatedList = StringUtils.join(meterIdList, ",");
			requiredEnergyContexts = DeviceAPI.getSpecificEnergyMeters(commaSeparatedList);
		}
		
		return requiredEnergyContexts;
	}
	
	public static List<AnalyticsAnomalyConfigContext> getAllAssetConfigs(String moduleName, long orgID) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		modBean.getModule(moduleName);
		List<AnalyticsAnomalyConfigContext> listOfReadings=new ArrayList<AnalyticsAnomalyConfigContext>();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getAnomalyConfigFields())
				.table(anomalyConfigTable)
				.andCustomWhere(" ORGID=? AND IS_ACTIVE=1",  orgID);
	
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


	public static LinkedHashSet<Long> getExistingAnomalyIDs(String moduleName,  String anomalyIDList) throws Exception{
		LinkedHashSet<Long> setOfAnomalyIDs=new LinkedHashSet<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		modBean.getModule(moduleName);
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
	
	public static Map<Long,List<Map<String,Object>>> getReadings(long startTime, long endTime, String siteIdList) throws Exception {
		ModuleBean modBean= (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String moduleName = FacilioConstants.ContextNames.WEATHER_READING;
		SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
				.select(modBean.getAllFields(moduleName))
				.moduleName(moduleName)
				.beanClass(ReadingContext.class)
				.andCustomWhere("TTIME >= ? AND TTIME < ? AND PARENT_ID IN  (" + siteIdList + ")",
						startTime, endTime);

		List<Map<String,Object>> weatherReadings= builder.getAsProps();
		
		Map<Long,List<Map<String,Object>>> siteVsWeatherData= new HashMap<Long,List<Map<String,Object>>>();
		for(Map<String,Object> weatherReading:weatherReadings) {
			
			Long siteId=(Long)weatherReading.remove("parentId");
			List<Map<String,Object>> siteData= siteVsWeatherData.get(siteId);
			if(siteData==null) {
				siteData= new ArrayList<Map<String,Object>>();
				siteVsWeatherData.put(siteId, siteData);
			}
			siteData.add(weatherReading);
		}
		return siteVsWeatherData;
	}
	
	public static List<TemperatureContext> getWeatherReadingsForOneSite(long startTime, long endTime, long siteIdOfEnergyMeter) throws Exception {
		ModuleBean modBean= (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String moduleName = FacilioConstants.ContextNames.WEATHER_READING;
		SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
				.select(modBean.getAllFields(moduleName))
				.moduleName(moduleName)
				.beanClass(ReadingContext.class)
				.andCustomWhere("TTIME >= ? AND TTIME < ? AND PARENT_ID = ? ",
						startTime, endTime, siteIdOfEnergyMeter);

		List<Map<String,Object>> weatherReadings= builder.getAsProps();

		if (weatherReadings == null || weatherReadings.isEmpty()) {
			return null;
		}
			
		Map<Long,List<Map<String,Object>>> siteVsWeatherData= new HashMap<Long,List<Map<String,Object>>>();
		for(Map<String,Object> weatherReading:weatherReadings) {
			
			Long siteId=(Long)weatherReading.remove("parentId");
			List<Map<String,Object>> siteData= siteVsWeatherData.get(siteId);
			
			if(siteData==null) {
				siteData= new ArrayList<Map<String,Object>>();
				siteVsWeatherData.put(siteId, siteData);
			}
			
			siteData.add(weatherReading);
		}
		
        List<TemperatureContext> siteWeatherReadings= TemperatureContext.convertToObjectFromMap(siteVsWeatherData.get(siteIdOfEnergyMeter));
        return siteWeatherReadings;
	}

	public static long getStartTime(long endTime, AnalyticsAnomalyConfigContext meterConfigContext) {
		long startTime=0;
		if (!meterConfigContext.getStartDateMode()) {
			startTime = endTime - (meterConfigContext.getHistoryDays() * 24 * 60 * 60 * 1000L);
		}else {
			startTime = DateTimeUtil.getDayStartTime(meterConfigContext.getStartDate(), "yyyy-MM-dd");
		}
		return startTime;
	}

	public static String getWeatherFileName(long meterID, Long siteId, long orgID) {

		long currentTimeInSecs=System.currentTimeMillis() / 1000;
		
		String weatherBaseFileName = meterID + "_"+ orgID + "_" + siteId +  "_" + currentTimeInSecs + "_weather.txt";
	
		return weatherBaseFileName;
	}
	
	public static String getEnergyFileName(long meterID, long orgID) {
		long currentTimeInSecs=System.currentTimeMillis() / 1000;
		String energyFileName = meterID + "_"+ orgID + "_" + currentTimeInSecs + "_meter.txt";
		return energyFileName;
	}
	
	public static boolean isDevEnviroment( ) {
		return ("development".equalsIgnoreCase(AwsUtil.getConfig("environment")));
	}

	public static String getWeatherAbsoluteFilePath(long meterID, long siteId, long orgID) {
		String tempDir = AwsUtil.getConfig("anomalyTempDir");
		String weatherFileName = tempDir +  File.separator + getWeatherFileName(meterID, siteId, orgID); 
		return weatherFileName;
	}
}
	