package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.PreAlarmOccurrenceContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

public class WorkflowRuleHistoricalAlarmsAPI {
	
	public static DateRange deleteAlarmOccurrencesEndToEnd(String messageKey, long startTime, long endTime, Type type) throws Exception 
	{		
		List<Long> distinctAlarmOccurrenceIds = getDistinctOccurrenceIdsFromEventsInExactWindow(messageKey, startTime, endTime, type);
		deleteAllEventsInExactWindow(messageKey, startTime, endTime, type);

		List<AlarmOccurrenceContext> alarmOccurrenceList = getAlarmOccurrencesAtEdgeCases(distinctAlarmOccurrenceIds);
		
		if (alarmOccurrenceList != null && !alarmOccurrenceList.isEmpty())
		{
			AlarmOccurrenceContext lastAlarmOccurrence = alarmOccurrenceList.get(alarmOccurrenceList.size() - 1);
			BaseAlarmContext baseAlarm =  lastAlarmOccurrence.getAlarm();
			BaseAlarmContext baseAlarmContext = NewAlarmAPI.getBaseAlarmById(baseAlarm.getId());
			
			if (alarmOccurrenceList.get(0).getCreatedTime() < startTime) 
			{
				AlarmOccurrenceContext initialEdgeAlarmOccurrence = alarmOccurrenceList.get(0);				
				clearAlarmOccurrenceIdForEdgeEvents(initialEdgeAlarmOccurrence, initialEdgeAlarmOccurrence.getCreatedTime(), startTime-1000); 
				startTime = initialEdgeAlarmOccurrence.getCreatedTime();
			}
			if (lastAlarmOccurrence.getClearedTime() == -1 || lastAlarmOccurrence.getClearedTime() > endTime) 
			{
				BaseEventContext finalEvent = getFinalEventForAlarmOccurrence(lastAlarmOccurrence);
				if(finalEvent != null && finalEvent.getCreatedTime() > endTime) {
					clearAlarmOccurrenceIdForEdgeEvents(lastAlarmOccurrence, endTime+1000, finalEvent.getCreatedTime()); //avoid event deletion
					endTime = finalEvent.getCreatedTime();
				}
			}	

			deleteAllAlarmOccurences(baseAlarmContext,distinctAlarmOccurrenceIds,startTime,endTime);
			NewAlarmAPI.updateBaseAlarmWithNoOfOccurences(baseAlarm);	
			NewAlarmAPI.changeLatestAlarmOccurrence(baseAlarmContext);		
		}
		
		return new DateRange(startTime, endTime);	
	}	
	
	private static List<Long> getDistinctOccurrenceIdsFromEventsInExactWindow(String messageKey, long startTime, long endTime, Type type) throws Exception {
		
		List<Long> distinctAlarmOccurrenceIds = new ArrayList<Long>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = NewEventAPI.getEventModuleName(type);
		FacilioModule eventModule = modBean.getModule(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(eventModule.getName()));
		
		FacilioField selectDistinctField = fieldMap.get("alarmOccurrence");
		selectDistinctField.setColumnName("DISTINCT("+fieldMap.get("alarmOccurrence").getCompleteColumnName()+")");
		
		SelectRecordsBuilder<BaseEventContext> selectEventbuilder = new SelectRecordsBuilder<BaseEventContext>()
				.select(Collections.singletonList(selectDistinctField))
				.module(eventModule)
				.beanClass(BaseEventContext.class)
				.setAggregation()
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("messageKey"), messageKey, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), startTime+","+endTime, DateOperators.BETWEEN))	
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("eventType"), String.valueOf(type.getIndex()), NumberOperators.EQUALS))
				.orderBy("CREATED_TIME");
		
		List<BaseEventContext> eventList = selectEventbuilder.get();
		for(BaseEventContext event :eventList) {
			distinctAlarmOccurrenceIds.add(event.getAlarmOccurrence().getId());	
		}
		return distinctAlarmOccurrenceIds;	
	}
	
	public static List<AlarmOccurrenceContext> getAlarmOccurrencesAtEdgeCases(List<Long> distinctAlarmOccurrenceIds) throws Exception {

		if (distinctAlarmOccurrenceIds != null && !distinctAlarmOccurrenceIds.isEmpty())
		{
			List<Long> edgeCaseAlarmOccurrenceIds = new ArrayList<Long>();
			edgeCaseAlarmOccurrenceIds.add(distinctAlarmOccurrenceIds.get(0));
			edgeCaseAlarmOccurrenceIds.add(distinctAlarmOccurrenceIds.get(distinctAlarmOccurrenceIds.size() - 1));
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
			List<FacilioField> allFields = modBean.getAllFields(module.getName());
		
			SelectRecordsBuilder<AlarmOccurrenceContext> selectbuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
					.select(allFields)
					.module(module)
					.beanClass(AlarmOccurrenceContext.class)
					.andCondition(CriteriaAPI.getIdCondition(edgeCaseAlarmOccurrenceIds, module))
					.orderBy("CREATED_TIME");
			
			List<AlarmOccurrenceContext> alarmOccurrenceList = selectbuilder.get();
			return alarmOccurrenceList;	
		}
		return null;				
	}
	
	public static void clearAlarmOccurrenceIdForEdgeEvents(AlarmOccurrenceContext edgeAlarmOccurrence, long startTime, long endTime) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		List<FacilioField> allEventFields = modBean.getAllFields(eventModule.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allEventFields);
		
		List<FacilioField> eventFields = new ArrayList<FacilioField>();
		eventFields.add(fieldMap.get("baseAlarm"));	
		eventFields.add(fieldMap.get("alarmOccurrence"));
		
		UpdateRecordBuilder<BaseEventContext> updateBuilder = new UpdateRecordBuilder<BaseEventContext>()
				.module(eventModule)
				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence","" +edgeAlarmOccurrence.getId(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
				.fields(eventFields);
		
		BaseAlarmContext baseAlarmNull = new BaseAlarmContext();
		baseAlarmNull.setId(-99);
		
		AlarmOccurrenceContext alarmOccurrenceContextNull = new AlarmOccurrenceContext();
		alarmOccurrenceContextNull.setId(-99);
		
		Map<String, Object> map = new HashMap<>();
		map.put("baseAlarm", FieldUtil.getAsProperties(baseAlarmNull));
		map.put("alarmOccurrence", FieldUtil.getAsProperties(alarmOccurrenceContextNull));
		updateBuilder.updateViaMap(map);
	}

	public static BaseEventContext getFinalEventForAlarmOccurrence(AlarmOccurrenceContext finalEdgeCaseAlarmOccurrence) throws Exception {
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		
		SelectRecordsBuilder<BaseEventContext> selectEventbuilder = new SelectRecordsBuilder<BaseEventContext>()
				.select(modBean.getAllFields(eventModule.getName()))
				.module(modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT))
				.beanClass(BaseEventContext.class)
				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence","" +finalEdgeCaseAlarmOccurrence.getId(), NumberOperators.EQUALS))
				.orderBy("CREATED_TIME DESC").limit(1);
		
		return selectEventbuilder.fetchFirst();
	}
	
	public static void deleteAllEventsInExactWindow(String messageKey,long startTime,long endTime, Type type) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = NewEventAPI.getEventModuleName(type);
		FacilioModule eventModule = modBean.getModule(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(eventModule.getName()));
	
		DeleteRecordBuilder<BaseEventContext> deleteBuilder = new DeleteRecordBuilder<BaseEventContext>()
				.module(eventModule)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("messageKey"), messageKey, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), startTime+","+endTime, DateOperators.BETWEEN))	
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("eventType"), String.valueOf(type.getIndex()), NumberOperators.EQUALS));
		
		deleteBuilder.delete();	
	}
	
	public static void deleteAllAlarmOccurences(BaseAlarmContext baseAlarmContext, List<Long> distinctAlarmOccurrenceIds, long startTime, long endTime) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);

		DeleteRecordBuilder<AlarmOccurrenceContext> deleteBuilder = new DeleteRecordBuilder<AlarmOccurrenceContext>()
				.module(module)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(distinctAlarmOccurrenceIds, module))
				.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", "" +baseAlarmContext.getId(), NumberOperators.EQUALS));
	
		Criteria criteria = new Criteria();
		criteria.addOrCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN));
		criteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", startTime+","+endTime, DateOperators.BETWEEN));	
		
		deleteBuilder.andCriteria(criteria);
		deleteBuilder.delete();			
	}
	
	public static void handleDuplicateTriggerMetricReadingErrors(Boolean isManualFailed, List<ReadingContext> readings, FacilioField field, ResourceContext currentResourceContext) throws Exception 
	{
		List<Long> uniqueTtimeOfReadings = new ArrayList<Long>();
		for(ReadingContext reading:readings) 
		{
			if(uniqueTtimeOfReadings != null && reading.getTtime() != - 1 && uniqueTtimeOfReadings.contains(reading.getTtime()) && field != null)
			{
				isManualFailed = true;
				String fieldReading = "";
				if(field.getName() != null && reading.getReading(field.getName()) != null) 
				{
					fieldReading = " - " + reading.getReading(field.getName());
					if(field instanceof NumberField) {
						Unit fieldUnit = UnitsUtil.getDisplayUnit((NumberField)field);
						if(fieldUnit != null && fieldUnit.getSymbol() != null) {
							fieldReading = fieldReading + " " +fieldUnit.getSymbol();							
						}
					}							
				}
				throw new Exception("Trigger metric readings (" + field.getDisplayName() +fieldReading+ 
						") for this asset (" +currentResourceContext.getName()+ ") seems to have duplicates at "
						+DateTimeUtil.getFormattedTime(reading.getTtime(), "dd-MMM-yyyy HH:mm")+ ".");
			}
			else {
				uniqueTtimeOfReadings.add(reading.getTtime());
			}
		}
	}
	
	public static DateRange deleteEntireAlarmOccurrences(long ruleId, long resourceId, long startTime, long endTime, AlarmOccurrenceContext.Type type) throws Exception 
	{		
		List<AlarmOccurrenceContext> alarmOccurrenceList = NewAlarmAPI.getAllAlarmOccurrences(ruleId, startTime, endTime, resourceId, type);	
//		deleteAllEventsInExactWindow(ruleId, resourceId, startTime, endTime, type);
		if (alarmOccurrenceList != null && !alarmOccurrenceList.isEmpty())
		{
			AlarmOccurrenceContext lastAlarmOccurrence = alarmOccurrenceList.get(alarmOccurrenceList.size() - 1);
			BaseAlarmContext baseAlarm =  lastAlarmOccurrence.getAlarm();
			BaseAlarmContext baseAlarmContext = NewAlarmAPI.getBaseAlarmById(baseAlarm.getId());
			
			if (alarmOccurrenceList.get(0).getCreatedTime() < startTime) 
			{
				AlarmOccurrenceContext initialEdgeAlarmOccurrence = alarmOccurrenceList.get(0);				
				clearAlarmOccurrenceIdForEdgeEvents(initialEdgeAlarmOccurrence, initialEdgeAlarmOccurrence.getCreatedTime(), startTime-1000); 
				startTime = initialEdgeAlarmOccurrence.getCreatedTime();
			}
			if (lastAlarmOccurrence.getClearedTime() == -1 || lastAlarmOccurrence.getClearedTime() > endTime) 
			{
				BaseEventContext finalEvent = getFinalEventForAlarmOccurrence(lastAlarmOccurrence);
				if(finalEvent != null && finalEvent.getCreatedTime() > endTime) {
					clearAlarmOccurrenceIdForEdgeEvents(lastAlarmOccurrence, endTime+1000, finalEvent.getCreatedTime()); //avoid event deletion
					endTime = finalEvent.getCreatedTime();
				}
			}	
			
//			deleteAllAlarmOccurences(baseAlarmContext, startTime, endTime);
			NewAlarmAPI.updateBaseAlarmWithNoOfOccurences(baseAlarm);	
			NewAlarmAPI.changeLatestAlarmOccurrence(baseAlarmContext);		
		}	
		return new DateRange(startTime, endTime);	
	}	
}
