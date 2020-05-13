package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ExecuteSensorRuleCommand;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.context.SensorAlarmContext;
import com.facilio.bmsconsole.context.SensorRuleContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleAlarmMeta;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
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

public class SensorRuleUtil {
	
	private static final Logger LOGGER = Logger.getLogger(SensorRuleUtil.class.getName());
	
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
			Map<Long, ReadingRuleAlarmMeta> metaMap = new HashMap<>();
			for (SensorAlarmContext sensorAlarm : sensorAlarms) {
				ReadingRuleAlarmMeta alarmMeta = constructNewAlarmMeta(sensorAlarm.getId(), sensorRule, sensorAlarm.getResource(), sensorAlarm.getSeverity().getSeverity().equals(FacilioConstants.Alarm.CLEAR_SEVERITY), sensorAlarm.getSubject());
				metaMap.put(alarmMeta.getResourceId(), alarmMeta);
			}
			sensorRule.setAlarmMetaMap(metaMap);
		}
	}
	
	public static ReadingRuleAlarmMeta constructNewAlarmMeta (long alarmId, SensorRuleContext sensorRule, ResourceContext resource, boolean isClear, String subject) {
		ReadingRuleAlarmMeta meta = new ReadingRuleAlarmMeta();
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
	
	public static Long fetchSingleReadingContext(NumberField numberField, long resourceId, long ttime) throws Exception{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(numberField.getModule().getName()));
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(modBean.getAllFields(numberField.getModule().getName()))
				.module(numberField.getModule())
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get(numberField.getName()),CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(resourceId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), String.valueOf(ttime), NumberOperators.LESS_THAN))
				.orderBy("TTIME").limit(1)
				.skipUnitConversion();
			
		ReadingContext readingContext = selectBuilder.fetchFirst();
		if(readingContext.getReading(numberField.getName()) != null)
		{
			return (Long)readingContext.getReading(numberField.getName());
		}
		return null;
	}
	
	public static List<Long> getReadingsBtwDayTimeInterval(NumberField numberField, long resourceId, long endTime, int noOfHoursToBeFetched) throws Exception 
	{	
//		long lastNdaysEndTime = DateTimeUtil.getDayStartTimeOf(endTime);
//		long lastNdaysStartTime = DateTimeUtil.getDayStartTimeOf(lastNdaysEndTime - (Integer.valueOf(noOfHoursToBeFetched) * 3600 * 1000));	

		long lastNdaysStartTime = endTime - (Integer.valueOf(noOfHoursToBeFetched) * 3600 * 1000);	//Hours to ms
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(numberField.getModule().getName()));
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(modBean.getAllFields(numberField.getModule().getName()))
				.module(numberField.getModule())
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get(numberField.getName()),CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(resourceId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), lastNdaysStartTime+","+(endTime-1000), DateOperators.BETWEEN))
				.skipUnitConversion()
				.orderBy("TTIME");
						
		List<ReadingContext> readingContexts = selectBuilder.get();
		List<Long> readings = new ArrayList<Long>();
		
		for(ReadingContext readingContext: readingContexts)
		{
			if(readingContext.getReading(numberField.getName()) != null)
			{
				readings.add((long)readingContext.getReading(numberField.getName()));
			}			
		}
		return readings;	
	}

}
