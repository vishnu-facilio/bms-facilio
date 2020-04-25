package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.PreAlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
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
import com.facilio.time.DateRange;

public class WorkflowRuleHistoricalAlarmsDeletionAPI {
	
	public static DateRange deleteEntireAlarmOccurrences(long ruleId, long resourceId, long startTime, long endTime, AlarmOccurrenceContext.Type type) throws Exception 
	{		
		List<AlarmOccurrenceContext> alarmOccurrenceList = NewAlarmAPI.getAllAlarmOccurrences(ruleId, startTime, endTime, resourceId, type);
		
		deleteAllEventsInExactWindow(ruleId, resourceId, startTime, endTime, type);
	
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
			
			deleteAllAlarmOccurences(baseAlarmContext, startTime, endTime);
			NewAlarmAPI.updateBaseAlarmWithNoOfOccurences(baseAlarm);	
			NewAlarmAPI.changeLatestAlarmOccurrence(baseAlarmContext);		
		}
		
		return new DateRange(startTime, endTime);	
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
	
	public static void deleteAllAlarmOccurences(BaseAlarmContext baseAlarmContext, long startTime, long endTime) throws Exception {
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
	
		DeleteRecordBuilder<AlarmOccurrenceContext> deleteBuilder = new DeleteRecordBuilder<AlarmOccurrenceContext>()
				.module(module)
				.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", "" +baseAlarmContext.getId(), NumberOperators.EQUALS));
	
		Criteria criteria = new Criteria();
		criteria.addOrCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN));
		criteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", startTime+","+endTime, DateOperators.BETWEEN));	
		
		deleteBuilder.andCriteria(criteria);
		deleteBuilder.delete();			
	}
	
	public static void deleteAllEventsInExactWindow(long ruleId, long resourceId,long startTime,long endTime, AlarmOccurrenceContext.Type type) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = null;
		if(AlarmOccurrenceContext.Type.READING == type) {
			moduleName = "readingevent";
		}
		else if(AlarmOccurrenceContext.Type.PRE_OCCURRENCE == type) {
			moduleName = "preevent";
		}
		
		if(moduleName == null) {
			return;
		}
		FacilioModule eventModule = modBean.getModule(moduleName);
	
		DeleteRecordBuilder<BaseEventContext> deleteBuilder = new DeleteRecordBuilder<BaseEventContext>()
				.module(eventModule)
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resourceId", ""+resourceId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN));	
		
		deleteBuilder.delete();	
	}
}
