package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpEventContext;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalAlarmsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateRange;

public class ExecuteSensorRuleHistoryCommand extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(ExecuteSensorRuleHistoryCommand.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception {
		
		try {
			JSONObject jobProps = BmsJobUtil.getJobProps(jc.getJobId(), "ExecuteSensorRuleHistoryJob");
		     
			Long startTime = (Long) jobProps.get(FacilioConstants.ContextNames.START_TIME);
			Long endTime = (Long) jobProps.get(FacilioConstants.ContextNames.END_TIME);
			Long assetCategoryId = (Long) jobProps.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
			List<Long> assetIds = (List<Long>) jobProps.get(FacilioConstants.ContextNames.ASSET_ID);

			if(assetCategoryId == null || startTime == null || endTime == null || endTime == -1 || startTime == -1) {
				throw new IllegalArgumentException("Insufficient params in job to execute sensor rule history");
			}	
			DateRange dateRange = new DateRange(startTime,endTime);
			if(assetIds == null || assetIds.isEmpty()) {
				List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(assetCategoryId);
				assetIds = assets.stream().map(asset -> asset.getId()).collect(Collectors.toList());
			}
			
			DateRange modifiedDateRange = WorkflowRuleHistoricalAlarmsAPI.deleteAllAlarmOccurrencesBasedonCriteria(getOccurrenceDeletionCriteria(assetIds, Type.SENSOR_ROLLUP_ALARM), getEventsProcessingCriteria(assetIds, Type.SENSOR_ROLLUP_ALARM), dateRange.getStartTime(), dateRange.getEndTime(), Type.SENSOR_ROLLUP_ALARM, null);
			WorkflowRuleHistoricalAlarmsAPI.deleteAllAlarmOccurrencesBasedonCriteria(getOccurrenceDeletionCriteria(assetIds, Type.SENSOR_ALARM), getEventsProcessingCriteria(assetIds, Type.SENSOR_ALARM), modifiedDateRange.getStartTime(), modifiedDateRange.getEndTime(), Type.SENSOR_ALARM, null);
	
			List<SensorRuleContext> sensorRules = SensorRuleUtil.getSensorRuleByCategoryId(assetCategoryId, null, true, true);
			List<ReadingContext> readings = new ArrayList<ReadingContext>();
			List<SensorRollUpEventContext> sensorMeterRollUpEvents = new ArrayList<SensorRollUpEventContext>();
			if(sensorRules != null && !sensorRules.isEmpty()) {	
				Set<FacilioField> sensorRuleFields = sensorRules.stream().map(sensorRule -> sensorRule.getSensorField()).collect(Collectors.toSet());
				LinkedHashMap<FacilioModule, List<FacilioField>> sensorRuleModuleVsFieldsMap = SensorRuleUtil.groupSensorRuleFieldsByModule(sensorRuleFields);
				for(FacilioModule module: sensorRuleModuleVsFieldsMap.keySet()) 
				{
					List<FacilioField> sensorRuleModuleFields = sensorRuleModuleVsFieldsMap.get(module);
					List<ReadingContext> fieldReadings = SensorRuleUtil.fetchReadingsForSensorRuleField(module, sensorRuleModuleFields, assetIds, dateRange.getStartTime(), dateRange.getEndTime());
					if(fieldReadings != null && !fieldReadings.isEmpty()) {
						readings.addAll(fieldReadings);
					}	
				}
//				SensorRuleUtil.executeSensorRules(sensorRules,readings, true, sensorMeterRollUpEvents);
			}
				
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error in ExecuteSensorRuleHistoryCommand -- "+
					" Exception: " + e.getMessage() , e);
		}	
	}
	
	private Criteria getOccurrenceDeletionCriteria(List<Long> resourceIds, Type type) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		AlarmOccurrenceContext.Type alarmOccurrenceType = NewAlarmAPI.getOccurrenceTypeFromAlarmType(type);
		FacilioModule module = modBean.getModule(NewAlarmAPI.getOccurrenceModuleName(alarmOccurrenceType));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
		Criteria deletionCriteria = new Criteria();
		deletionCriteria.addAndCondition(CriteriaAPI.getConditionFromList("RESOURCE_ID", "resource", resourceIds, NumberOperators.EQUALS));

		return deletionCriteria;
	}
	
	private Criteria getEventsProcessingCriteria(List<Long> resourceIds, Type type) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = NewEventAPI.getEventModuleName(type);
		FacilioModule eventModule = modBean.getModule(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(eventModule.getName()));
		Criteria fetchCriteria = new Criteria();
		fetchCriteria.addAndCondition(CriteriaAPI.getConditionFromList("RESOURCE_ID", "resource", resourceIds, NumberOperators.EQUALS));
		return fetchCriteria;
	}

}
