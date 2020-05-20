package com.facilio.bmsconsole.context.sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ExecuteSensorRuleCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleAlarmMeta;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.UnitsUtil;

public class SensorRuleUtil {
	
	private static final Logger LOGGER = Logger.getLogger(SensorRuleUtil.class.getName());	
	
	public static List<SensorRuleContext> getSensorRuleByIds(List<Long> ruleIds) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(FieldFactory.getSensorRuleFields())
			.table(ModuleFactory.getSensorRuleModule().getTableName())
			.andCondition(CriteriaAPI.getIdCondition(ruleIds, ModuleFactory.getSensorRuleModule()));
							
		List<Map<String, Object>> props = selectBuilder.get();
		List<SensorRuleContext> sensorRuleList = getSensorRuleFromProps(props, true);
		if (sensorRuleList != null && !sensorRuleList.isEmpty()) {
			for(SensorRuleContext sensorRule: sensorRuleList) {
				setMatchedResourcesIds(sensorRule);
			}
		}
		return sensorRuleList;
	}
	
	private static void setMatchedResourcesIds(SensorRuleContext sensorRule) throws Exception {
		List<AssetContext> categoryAssets = AssetsAPI.getAssetListOfCategory(sensorRule.getAssetCategoryId());
		List<Long> assetIds = categoryAssets.stream().map(asset -> asset.getId()).collect(Collectors.toList());
		sensorRule.setMatchedResourceIds(assetIds);
	}
	
	public static List<SensorRuleContext> fetchSensorRulesByModule(String moduleName, boolean isFetchSubProps) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule readingModule = modBean.getModule(moduleName);
		FacilioModule categoryModule = modBean.getParentModule(readingModule.getModuleId());
		List<SensorRuleContext> sensorRules = SensorRuleUtil.getSensorRuleByModuleId(categoryModule,isFetchSubProps);
		return sensorRules;
	}
	
	public static List<SensorRuleContext> getSensorRuleByModuleId(FacilioModule childModule, boolean isFetchSubProps) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSensorRuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(FieldFactory.getSensorRuleFields())
			.table(ModuleFactory.getSensorRuleModule().getTableName())
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), childModule.getExtendedModuleIds(), NumberOperators.EQUALS));
							
		List<Map<String, Object>> props = selectBuilder.get();
		return getSensorRuleFromProps(props, isFetchSubProps);
	}
	
	public static List<SensorRuleContext> getSensorRuleFromProps(List<Map<String, Object>> props, boolean isFetchSubProps) throws Exception {
		
		if (props != null && !props.isEmpty()) {
			
			List<SensorRuleContext> sensorRules = FieldUtil.getAsBeanListFromMapList(props, SensorRuleContext.class);
			
			if(isFetchSubProps) 
			{
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<Long> fieldIds = new ArrayList<Long>();
				List<Long> assetCategories = new ArrayList<Long>();

				for(SensorRuleContext sensorRule: sensorRules) {		
					fieldIds.add(sensorRule.getReadingFieldId());
					assetCategories.add(sensorRule.getAssetCategoryId());
				}
				
				List<FacilioField> fields = modBean.getFields(fieldIds);
				Map<Long, FacilioField> fieldMap = FieldFactory.getAsIdMap(fields);
				
				for(SensorRuleContext sensorRule: sensorRules) 
				{		
					sensorRule.setReadingField(fieldMap.get(sensorRule.getReadingFieldId()));
					sensorRule.setModule(modBean.getModule(sensorRule.getModuleId()));
					sensorRule.getReadingField().setModule(sensorRule.getModule());
					fetchAlarmMeta(sensorRule);
				}
			}
			return sensorRules;
		}
		return null;		
	}
	
	public static void executeSensorRules(List<SensorRuleContext> sensorRules, List<ReadingContext> readings, boolean isHistorical) throws Exception {
		if(sensorRules != null && !sensorRules.isEmpty()) 
		{		
			List<Long> sensorRuleIds = sensorRules.stream().map(sensorRule -> sensorRule.getId()).collect(Collectors.toList());
			HashMap<Long, JSONObject> sensorRuleValidatorPropsMap = SensorRuleUtil.getSensorRuleValidatorPropsByParentRuleIds(sensorRuleIds);		
			LinkedHashMap<String, List<ReadingContext>> historicalReadingsMap = new LinkedHashMap<String, List<ReadingContext>>();

			for(SensorRuleContext sensorRule:sensorRules) 
			{	
				if(isHistorical) {
					constructHistoryReadingsMap(readings, sensorRule, historicalReadingsMap);
				}
				List<SensorEventContext> sensorEvents= new ArrayList<SensorEventContext>();
				
				for(ReadingContext reading:readings) 
				{
					List<ReadingContext> historicalReadings = new ArrayList<ReadingContext>();
					historicalReadings = (isHistorical && !sensorRule.getSensorRuleTypeEnum().isCurrentValueDependent()) ? historicalReadingsMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), sensorRule.getReadingField())) : null;

					if(reading.getReading(sensorRule.getReadingField().getName()) != null) 
					{
						SensorRuleTypeValidationInterface validatorType = sensorRule.getSensorRuleTypeEnum().getSensorRuleValidationType();
						boolean result = validatorType.evaluateSensorRule(sensorRule, reading.getReadings(), sensorRuleValidatorPropsMap.get(sensorRule.getId()), isHistorical, historicalReadings);
						JSONObject defaultSeverityProps = sensorRule.getSensorRuleTypeEnum().getSensorRuleValidationType().getDefaultSeverityAndSubject();
						checkDefaultSeverityProps(defaultSeverityProps, sensorRuleValidatorPropsMap.get(sensorRule.getId()));
						
						if(result) {
							SensorEventContext sensorEvent = sensorRule.constructEvent(reading,sensorRuleValidatorPropsMap.get(sensorRule.getId()), defaultSeverityProps, isHistorical);
							sensorEvents.add(sensorEvent);
						}
						else {
							SensorEventContext sensorEvent = sensorRule.constructClearEvent(reading,sensorRuleValidatorPropsMap.get(sensorRule.getId()), defaultSeverityProps, isHistorical);
							if(sensorEvent != null) {
								sensorEvents.add(sensorEvent);
							}
						}
					}
				}
				
				FacilioChain addEventChain = TransactionChainFactory.getV2AddEventChain(true);
				addEventChain.getContext().put(EventConstants.EventContextNames.EVENT_LIST, sensorEvents);
				addEventChain.execute();
			}
		}			
	}
	
	private static void checkDefaultSeverityProps(JSONObject defaultSeverityProps, JSONObject ruleProps) {
		if(ruleProps.get("severity") != null && ruleProps.get("subject") != null) {
			defaultSeverityProps.put("severity", ruleProps.get("severity"));
			defaultSeverityProps.put("subject", ruleProps.get("subject"));
		}
	}
	
	private static void fetchAlarmMeta(SensorRuleContext sensorRule) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SENSOR_ALARM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		List<SensorAlarmContext> sensorAlarms = new SelectRecordsBuilder<SensorAlarmContext>()
												.select(fields)
												.beanClass(SensorAlarmContext.class)
												.moduleName(FacilioConstants.ContextNames.SENSOR_ALARM)
												.andCondition(CriteriaAPI.getCondition(fieldMap.get("sensorRule"), String.valueOf(sensorRule.getId()), PickListOperators.IS))
												.fetchSupplement((LookupField) fieldMap.get("severity"))
												.get();
		if (CollectionUtils.isNotEmpty(sensorAlarms)) {
			Map<Long, SensorRuleAlarmMeta> metaMap = new HashMap<>();
			for (SensorAlarmContext sensorAlarm : sensorAlarms) {
				SensorRuleAlarmMeta alarmMeta = constructNewAlarmMeta(sensorAlarm.getId(), sensorRule, sensorAlarm.getResource(), sensorAlarm.getSeverity().getSeverity().equals(FacilioConstants.Alarm.CLEAR_SEVERITY), sensorAlarm.getSubject());
				metaMap.put(alarmMeta.getResourceId(), alarmMeta);
			}
			sensorRule.setAlarmMetaMap(metaMap);
		}
	}
	
	public static SensorRuleAlarmMeta constructNewAlarmMeta (long alarmId, SensorRuleContext sensorRule, ResourceContext resource, boolean isClear, String subject) {
		SensorRuleAlarmMeta meta = new SensorRuleAlarmMeta();
		meta.setOrgId(AccountUtil.getCurrentOrg().getId());
		meta.setAlarmId(alarmId);
		meta.setRuleGroupId(sensorRule.getId());
		meta.setResourceId(resource.getId());
		meta.setResource(resource);
		meta.setReadingFieldId(sensorRule.getReadingFieldId());
		meta.setClear(isClear);
		if(!StringUtils.isEmpty(subject)) {
			meta.setSubject(subject);
		}
		return meta;
	}
	
	public static HashMap<Long, JSONObject> getSensorRuleValidatorPropsByParentRuleIds(List<Long> parentRuleIdList) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSensorRulePropsFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(FieldFactory.getSensorRulePropsFields())
			.table(ModuleFactory.getSensorRulePropsModule().getTableName())
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentSensorRuleId"), parentRuleIdList, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		HashMap<Long,JSONObject> sensorRulePropsMap = new HashMap<Long,JSONObject>();

		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props ) 
			{
				if(prop.get("parentSensorRuleId") != null && prop.get("ruleValidatorProps") != null) 
				{
					long parentSensorRuleId = (long) prop.get("parentSensorRuleId");
					JSONObject ruleValidatorProps = (JSONObject) prop.get("ruleValidatorProps");
					sensorRulePropsMap.put(parentSensorRuleId, ruleValidatorProps);
				}
			}
		
		}
		return sensorRulePropsMap;
	}
	
	public static boolean isAllowedSensorMetric(NumberField numberField) {
		
		List<Integer> allowedMetricIds = new ArrayList<Integer>();
		allowedMetricIds.add(Metric.TEMPERATURE.getMetricId());
		allowedMetricIds.add(Metric.ABSOLUTE_HUMIDITY.getMetricId());
		allowedMetricIds.add(Metric.SPECIFIC_HUMIDITY.getMetricId());
		allowedMetricIds.add(Metric.PRESSURE.getMetricId());
		allowedMetricIds.add(Metric.VAPOUR_PRESSURE.getMetricId());
		
		Integer fieldMetricId = numberField.getMetric();
		if(fieldMetricId != null && fieldMetricId != - 1) {
			if(allowedMetricIds.contains(fieldMetricId)){
				return true;
			}	
		}
		return false;
	}
	
	public static ReadingContext fetchSingleReadingContext(NumberField numberField, long resourceId, long ttime) throws Exception{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(numberField.getModule().getName()));
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(modBean.getAllFields(numberField.getModule().getName()))
				.module(numberField.getModule())
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get(numberField.getName()),CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(resourceId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), String.valueOf(ttime), NumberOperators.LESS_THAN))
				.orderBy("TTIME").limit(1);			
		ReadingContext readingContext = selectBuilder.fetchFirst();
		return readingContext;
	}
	
	public static List<ReadingContext> getReadingsBtwDayTimeInterval(NumberField numberField, long resourceId, long endTime, int noOfHoursToBeFetched) throws Exception 
	{	
//		long lastNdaysEndTime = DateTimeUtil.getDayStartTimeOf(endTime);
//		long lastNdaysStartTime = DateTimeUtil.getDayStartTimeOf(lastNdaysEndTime - (Integer.valueOf(noOfHoursToBeFetched) * 3600 * 1000));	
//		long lastNdaysStartTime = endTime - (Integer.valueOf(noOfHoursToBeFetched) * 3600 * 1000);	//Hours to ms
		
		long lastNdaysStartTime = DateTimeUtil.addHours(endTime, -1*noOfHoursToBeFetched);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(numberField.getModule().getName()));
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(modBean.getAllFields(numberField.getModule().getName()))
				.module(numberField.getModule())
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get(numberField.getName()),CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(resourceId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), lastNdaysStartTime+","+(endTime-1000), DateOperators.BETWEEN))
				.orderBy("TTIME");
						
		List<ReadingContext> readingContexts = selectBuilder.get();
		return readingContexts;	
	}
	
	public static List<Double> getLiveOrHistoryReadingsToBeEvaluated(NumberField numberField, long resourceId, long readingEndTime, int noOfHoursToBeFetched, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String,List<ReadingContext>> completeHistoricalReadingsMap) throws Exception{
		
		List<ReadingContext> readingsToBeEvaluated = new ArrayList<ReadingContext>(); 
		if(isHistorical) {
			String key = ReadingsAPI.getRDMKey(resourceId, numberField);
			List<ReadingContext> completeHistoricalReadings = completeHistoricalReadingsMap.get(key);
					
			if(historicalReadings != null && !historicalReadings.isEmpty() && completeHistoricalReadings == null) {
				completeHistoricalReadings = new ArrayList<ReadingContext>();
				completeHistoricalReadingsMap.put(key, completeHistoricalReadings);
				
				List<ReadingContext> bufferIntervalReadings = SensorRuleUtil.getReadingsBtwDayTimeInterval(numberField, resourceId, readingEndTime, noOfHoursToBeFetched);
				if(bufferIntervalReadings != null && !bufferIntervalReadings.isEmpty()) {
					completeHistoricalReadings.addAll(bufferIntervalReadings);
				}
				completeHistoricalReadings.addAll(historicalReadings);
			}
			
			if(completeHistoricalReadings != null && !completeHistoricalReadings.isEmpty()) 
			{
				long pastIntervalStartTime = DateTimeUtil.addHours(readingEndTime, -1*noOfHoursToBeFetched);
				for(ReadingContext historyReading :completeHistoricalReadings) {
					if(historyReading.getTtime() > pastIntervalStartTime && historyReading.getTtime() < readingEndTime) {
						readingsToBeEvaluated.add(historyReading);
					}	
				}
			}	
		}
		else {
			readingsToBeEvaluated = SensorRuleUtil.getReadingsBtwDayTimeInterval(numberField, resourceId, readingEndTime, noOfHoursToBeFetched);
		}	
		List<Double> readings = SensorRuleUtil.getReadings(readingsToBeEvaluated,numberField);
		return readings;	
	}
	
	public static List<ReadingContext> fetchReadingsForSensorRuleField(SensorRuleContext sensorRule, List<Long> resourceIds, long startTime, long endTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(sensorRule.getReadingField().getModule().getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
			.select(fields)
			.module(sensorRule.getReadingField().getModule())
			.beanClass(ReadingContext.class)
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), resourceIds, PickListOperators.IS))
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), startTime+","+endTime, DateOperators.BETWEEN))
			.andCondition(CriteriaAPI.getCondition(sensorRule.getReadingField(), CommonOperators.IS_NOT_EMPTY))
			.orderBy("TTIME")
			;
		
		return selectBuilder.get();
	}
	
	public static List<Double> getReadings(List<ReadingContext> readingContexts, NumberField numberField) {
		List<Double> readings = new ArrayList<Double>();
		if(readingContexts != null && !readingContexts.isEmpty()) {
			for(ReadingContext readingContext: readingContexts)
			{
				Object value = readingContext.getReading(numberField.getName());
				if(value != null){
					readings.add(Double.parseDouble(value.toString()));	
				}			
			}	
		}
		return readings;
	}
	
	private static void constructHistoryReadingsMap(List<ReadingContext> readings, SensorRuleContext sensorRule, LinkedHashMap<String, List<ReadingContext>> historicalReadingsMap) {
		for(ReadingContext reading: readings) 
		{
			String key = ReadingsAPI.getRDMKey(reading.getParentId(), sensorRule.getReadingField());
			List<ReadingContext> parentFieldReadingsList = historicalReadingsMap.get(key);
			if(parentFieldReadingsList == null) {
				parentFieldReadingsList = new ArrayList<ReadingContext>();
				historicalReadingsMap.put(key, parentFieldReadingsList);
			}
			parentFieldReadingsList.add(reading);
		}
	}
}
